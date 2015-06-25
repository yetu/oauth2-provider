import com.softwaremill.macwire.{Macwire, Wired}
import com.yetu.common.YetuCommonGlobalSettings
import com.yetu.oauth2provider.registry._
import com.yetu.oauth2provider.utils.{Config, CorsFilter}
import play.api.mvc.EssentialAction

object Global extends YetuCommonGlobalSettings with Macwire {

  private val diRegistry: Wired = {
    if (Config.persist) {
      wiredInModule(PersistentControllerRegistry)
    } else {
      wiredInModule(MemoryControllerRegistry)
    }
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    diRegistry.lookupSingleOrThrow(controllerClass)
  }

  override def doFilter(action: EssentialAction) = CorsFilter(action)

}
