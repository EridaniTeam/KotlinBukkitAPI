package org.kotlinmc.bukkit.dsl.scoreboard

import org.kotlinmc.bukkit.collections.onlinePlayerMapOf
import org.kotlinmc.bukkit.extensions.WithPlugin
import org.kotlinmc.bukkit.extensions.scheduler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective

@DslMarker
@Retention(AnnotationRetention.BINARY)
annotation class ScoreboardDSLMarker

@ScoreboardDSLMarker
inline fun WithPlugin<*>.scoreboard(
        title: String,
        block: ScoreboardDSLBuilder.() -> Unit
) = plugin.scoreboard(title, block)

@ScoreboardDSLMarker
inline fun Plugin.scoreboard(
        title: String,
        block: ScoreboardDSLBuilder.() -> Unit
) = scoreboard(title, this, block)

/**
 *
 * onRender events: Called when show/set the scoreboard to a player
 * onUpdate events: Called when updateDelay trigger or force update by using [ScoreboardDSL.updateTitle],
 * [ScoreboardDSL.updateLine], [ScoreboardDSL.updateLines].
 */
@ScoreboardDSLMarker
inline fun scoreboard(
        title: String,
        plugin: Plugin,
        block: ScoreboardDSLBuilder.() -> Unit
): ScoreboardDSL = ScoreboardDSLBuilder(plugin, title).apply(block)

val linesBounds = 1..16

interface ScoreboardDSL {

    val players: Map<Player, Objective>

    /**
     * Show/set the built scoreboard to a [player]
     */
    fun show(player: Player)

    /**
     * Update the title to all players with the scoreboard set see [show])
     */
    fun updateTitle()

    /**
     * Update a specific line to all players with the scoreboard set see [show])
     *
     * Returns false if the line doesn't exists, true if the line was founded and update.
     */
    fun updateLine(line: Int): Boolean

    /**
     * Update all lines to all players with the scoreboard set (see [show])
     */
    fun updateLines()

    /**
     * Remove all scoreboard of the players and cancel all internal tasks
     */
    fun dispose()
}

class ScoreboardDSLBuilder(internal val plugin: Plugin, var title: String) : ScoreboardDSL {

    private val lines = mutableMapOf<Int, ScoreboardLine>()
    private val _players = plugin.onlinePlayerMapOf<Objective>()
    override val players: Map<Player, Objective> = _players

    private var titleController: ScoreboardTitle? = null

    private var taskTitle: BukkitTask? = null
    private var taskLine: BukkitTask? = null

    var updateTitleDelay: Long = 0
        set(value) {
            field = value
            taskTitle?.cancel(); taskTitle = null
            if (value > 0 && _players.isNotEmpty())
                taskTitle = scheduler { updateTitle() }.runTaskTimer(plugin, 0, value)
        }

    var updateLinesDelay: Long = 0
        set(value) {
            field = value
            taskLine?.cancel(); taskLine = null
            if (value > 0 && _players.isNotEmpty())
                taskLine = scheduler { updateLines() }.runTaskTimer(plugin, 0, value)
        }

    override fun dispose() {
        taskTitle?.cancel()
        taskLine?.cancel()

        for (objective in players.values) {
            objective.unregister()
        }
    }

    fun line(line: Int, scoreboardLine: ScoreboardLine) {
        if (line in linesBounds)
            lines.put(line, scoreboardLine)
    }

    /**
     * set a [text] to specified [line] of the scoreboard.
     */
    fun line(line: Int, text: String) = line(line, text, block = {})

    /**
     * set a [text] to specified [line] (1 to 16) of the scoreboard with a builder.
     *
     * In the builder you can use [ScoreboardLine.onRender] to change the value
     * when the line renders to the player, or [ScoreboardLine.onUpdate] when you call [updateLine] or
     * specify a value greater then 0 to [updateTitleDelay] to update all lines periodic.
     *
     * If [line] be greater then 16 or less then 1 the line will be ignored.
     */
    @ScoreboardDSLMarker
    inline fun line(
            line: Int,
            text: String,
            block: ScoreboardLine.() -> Unit
    ) = line(line, ScoreboardLine(this, text).apply(block))

    /**
     * add a array of lines at scoreboard starting at the [startInLine] value.
     */
    fun lines(vararg lines: String, startInLine: Int = 1) {
        lines(*lines, startInLine = startInLine, block = {})
    }

    /**
     * Add a array of lines at scoreboard starting at the [startInLine] value with a builder.
     *
     * In the builder you can use [ScoreboardLine.onRender] to change the value
     * when the line renders to the player, or [ScoreboardLine.onUpdate] when you call [updateLine] or
     * specify a value greater then 0 to [updateTitleDelay] to update all lines periodic.
     */
    @ScoreboardDSLMarker
    inline fun lines(vararg lines: String, startInLine: Int = 1, block: ScoreboardLine.() -> Unit) {
        for ((index, line) in lines.withIndex()) {
            line(index + startInLine, line, block)
        }
    }

    fun remove(line: Int): Boolean {
        return lines.remove(line) != null
    }

    fun titleController(title: ScoreboardTitle) = title.also {
        titleController = it
    }

    /**
     * The DSL block to manage how the title of the scoreboard will be displayed to a specific player.
     */
    @ScoreboardDSLMarker
    inline fun title(block: ScoreboardTitle.() -> Unit) = titleController(ScoreboardTitle(this).apply(block))

    override fun show(player: Player) {
        val max = lines.keys.maxOrNull()
        if (max != null) {
            if (_players.get(player)?.scoreboard != null) return
            val sb = Bukkit.getScoreboardManager().newScoreboard

            val objective = sb.getObjective(DisplaySlot.SIDEBAR)
                    ?: sb.registerNewObjective("sidebar", "dummy").apply {
                        displaySlot = DisplaySlot.SIDEBAR
                        displayName = TitleRender(player, title).also { titleController?.renderEvent?.invoke(it) }.newTitle
                    }

            for (i in 1..max) {
                lineBuild(objective, i) { sbLine ->
                    if (sbLine.renderEvent != null) {
                        LineRender(player, sbLine.text).also { sbLine.renderEvent?.invoke(it) }.newText
                    } else sbLine.text
                }
            }

            player.scoreboard = sb
            _players.put(player, objective) {
                if (_players.isEmpty()) {
                    taskTitle?.cancel(); taskTitle = null
                    taskLine?.cancel(); taskLine = null
                }
            }

            if (taskTitle == null && updateTitleDelay > 0)
                updateTitleDelay = updateTitleDelay
            if (taskLine == null && updateLinesDelay > 0)
                updateLinesDelay = updateLinesDelay
        }
    }

    private val lineColors = (0..15).map {
        it.toByte().toString(2).take(4).map {
            if(it == '0') ChatColor.RESET.toString()
            else ChatColor.WHITE.toString()
        }.joinToString("")
    }

    private fun entryByLine(line: Int) = lineColors[line]

    private inline fun lineBuild(objective: Objective, line: Int, lineTextTransformer: (ScoreboardLine) -> String) {
        val sb = objective.scoreboard
        val sbLine = lines[line] ?: ScoreboardLine(this, "")

        val lineEntry = entryByLine(line)
        val realScoreLine = 17 - line

        val text = lineTextTransformer(sbLine)

        val team = sb.getTeam("line_$line") ?: sb.registerNewTeam("line_$line")

        if (text.isEmpty()) {
            if (!team.prefix.isNullOrEmpty()) team.prefix = ""
            if (!team.suffix.isNullOrEmpty()) team.suffix = ""
            return
        }

        if (text.length > 16) {
            val fixedText = if (text.length > 32) text.take(32) else text
            val prefix = fixedText.substring(0, 16)
            val suffix = fixedText.substring(16, fixedText.length-1)
            if (team.prefix != prefix || team.suffix != suffix) {
                team.prefix = prefix
                team.suffix = suffix
            }
        } else {
            if (team.prefix != text) {
                team.prefix = text
                team.suffix = ""
            }
        }

        if (!team.hasEntry(lineEntry)) team.addEntry(lineEntry)

        val score = objective.getScore(lineEntry)

        if (!(score.isScoreSet && score.score == realScoreLine)) {
            score.score = realScoreLine
        }
    }

    override fun updateTitle() {
        for ((player, objective) in _players) {
            val titleUpdate = TitleUpdate(player, title).also { titleController?.updateEvent?.invoke(it) }
            objective.displayName = titleUpdate.newTitle
        }
    }

    override fun updateLine(line: Int): Boolean {
        if (lines[line] == null) return false
        for ((player, objective) in _players) {
            lineBuild(objective, line) { sbLine ->
                if (sbLine.updateEvent != null) {
                    LineUpdate(player, sbLine.text).also { sbLine.updateEvent?.invoke(it) }.newText
                } else sbLine.text
            }
        }
        return true
    }

    override fun updateLines() {
        val max = lines.keys.maxOrNull()
        if (max != null) {
            for (i in 1..max) {
                updateLine(i)
            }
        }
    }
}

interface PlayerScoreboard { val player: Player }

interface ChangeableTitle { var newTitle: String }

class TitleRender(override val player: Player, override var newTitle: String) : PlayerScoreboard, ChangeableTitle
class TitleUpdate(override val player: Player, override var newTitle: String) : PlayerScoreboard, ChangeableTitle

typealias TitleRenderEvent = TitleRender.() -> Unit
typealias TitleUpdateEvent = TitleUpdate.() -> Unit

class ScoreboardTitle(private val scoreboard: ScoreboardDSLBuilder) {
    internal var renderEvent: TitleRenderEvent? = null
    internal var updateEvent: TitleUpdateEvent? = null

    @ScoreboardDSLMarker
    fun onRender(block: TitleRenderEvent) {
        renderEvent = block
    }

    @ScoreboardDSLMarker
    fun onUpdate(block: TitleUpdateEvent) {
        updateEvent = block
    }
}

interface LineChangeableText { var newText: String }

class LineRender(override val player: Player, override var newText: String) : PlayerScoreboard, LineChangeableText
class LineUpdate(override val player: Player, override var newText: String) : PlayerScoreboard, LineChangeableText

typealias LineRenderEvent = LineRender.() -> Unit
typealias LineUpdateEvent = LineUpdate.() -> Unit

class ScoreboardLine(private val scoreboard: ScoreboardDSLBuilder, text: String) {
    var text: String = text
        internal set

    internal var renderEvent: LineRenderEvent? = null
    internal var updateEvent: LineUpdateEvent? = null

    @ScoreboardDSLMarker
    fun onRender(block: LineRenderEvent) {
        renderEvent = block
    }

    @ScoreboardDSLMarker
    fun onUpdate(block: LineUpdateEvent) {
        updateEvent = block
    }
}
