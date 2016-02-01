package jubu.mailer

import java.util.Properties
import javax.mail.{Authenticator, PasswordAuthentication, Session}

import scala.annotation.tailrec

/**
	* @author jubu
	*/
trait SessionFactory {
	def append(p: Prop): SessionFactory

	def ::(p: Prop): SessionFactory = append(p)

	def +(p: Prop): SessionFactory = ::(p)

	/**
		*
		* @param credentials (username,password) pair
		* @return Plain mail Session
		*/
	def session(credentials: Option[(String, String)] = None): Session
}

object SessionFactory {
	def apply() = new SessionFactory {
		override def append(p: Prop): SessionFactory = {
			case class impl(prev: impl) extends SessionFactory {
				val prop = p

				override def append(p: Prop): SessionFactory = new impl(this)

				def properties(): Properties = {
					@tailrec
					def walk(prev: impl, properties: Properties = new Properties()): Properties = {
						prop.convert.map { p => properties.setProperty(p._1, p._2.toString) }
						if (prev.prev == null) properties else walk(prev, properties)
					}
					walk(this)
				}

				override def session(credentials: Option[(String, String)] = None): Session = credentials match {
					case None => Session.getInstance(properties())
					case Some((u, p)) => {
						val ps = properties()
						ps.put("mail.smtp.auth", true.toString)
						Session.getInstance(ps, new Authenticator {
							protected override def getPasswordAuthentication() = new PasswordAuthentication(u, p)
						})
					}
				}
			}
			impl(null)
		}

		override def session(credentials: Option[(String, String)] = None): Session = {
			throw new NotImplementedError()
		}
	}

}
