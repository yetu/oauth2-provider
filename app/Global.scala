import com.softwaremill.macwire.{ Macwire, Wired }
import com.yetu.oauth2provider.utils.{ Config, CorsFilter }
import play.api.GlobalSettings
import play.api.mvc.{ Result, RequestHeader, EssentialAction }

import com.yetu.oauth2provider.registry._

import scala.concurrent.Future

object Global extends GlobalSettings with Macwire {

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
