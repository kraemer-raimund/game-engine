import dev.rakrae.gameengine.core.Engine
import dev.rakrae.gameengine.samplegame.SampleGame

fun main() {
    val game = SampleGame()
    val engine = Engine(game)
    engine.start()
}
