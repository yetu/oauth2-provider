import com.softwaremill.macwire.{ Macwire, Wired }
import com.yetu.oauth2provider.registry.ControllerRegistry
import com.yetu.oauth2provider.utils.{ Config, CorsFilter }
import play.api.GlobalSettings
import play.api.mvc.EssentialAction

import com.yetu.oauth2provider.registry._

object Global extends GlobalSettings with Macwire {

  /**
   * Original, non-DI code to call constructors:
   *
   * An implementation that checks if the controller expects a RuntimeEnvironment and
   * passes the instance to it if required.
   * This can be replaced by any DI framework to inject it differently.
   */
  //  override def getControllerInstance[A](controllerClass: Class[A]): A = {
  //    val instance = controllerClass.getConstructors.find { c =>
  //      val params = c.getParameterTypes
  //      params.length == 1 && params(0) == classOf[RuntimeEnvironment[YetuUser]]
  //    }.map {
  //      _.asInstanceOf[Constructor[A]].newInstance(MyRuntimeEnvironment)
  //    }
  //    instance.getOrElse(super.getControllerInstance(controllerClass))
  //  }

  private val diRegistry: Wired = {
    Config.databaseToUse match {
      case "ldap" => {
        wiredInModule(LdapControllerRegistry)
      }
      case "inmemory" => {
        wiredInModule(MemoryControllerRegistry)
      }
      case _ => {
        throw new Exception("Configuration invalid: please specify either 'inmemory' or 'ldap' for the 'databaseToUse' configuration key.")
      }
    }
  }

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    diRegistry.lookupSingleOrThrow(controllerClass)
  }

  override def doFilter(action: EssentialAction) = CorsFilter(action)

}

