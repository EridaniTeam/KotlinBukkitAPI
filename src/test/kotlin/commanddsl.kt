import org.kotlinmc.bukkit.extensions.WithPlugin
import org.kotlinmc.bukkit.extensions.msg

fun WithPlugin<*>.test() {
    // /rootCommand
    "rootCommand" {
        // /rootCommand 1
        "1" {
            // /rootCommand 1 1
            "1" {
                executor {
                    sender.msg("Wow")
                }
            }
        }
    }
}