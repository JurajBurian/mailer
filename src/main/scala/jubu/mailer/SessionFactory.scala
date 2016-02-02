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

/**
	*
	*/
object SessionFactory {
	def apply() = new SessionFactory {
		override def append(p: Prop): SessionFactory = {
			@tailrec
			def walk(p: impl, properties: Properties = new Properties()): Properties = {
				p.prop.convert.map { p => properties.setProperty(p._1, p._2.toString) }
				if (p.prev == null) {
					properties
				} else {
					walk(p.prev, properties)
				}
			}
			case class impl(prev: impl, val prop:Prop) extends SessionFactory {
				override def append(p: Prop): SessionFactory = new impl(this, p)
				def properties(): Properties = {
					walk(this)
				}

				override def session(credentials: Option[(String, String)] = None): Session = {
					val ps = properties()
					credentials match {
						case None => {
							Session.getInstance(ps)
						}
						case Some((u, p)) => {
							ps.put("mail.smtp.auth", true.toString)
							Session.getInstance(ps, new Authenticator {
								protected override def getPasswordAuthentication() = new PasswordAuthentication(u, p)
							})
						}
					}
				}
			}
			impl(null, p)
		}

		override def session(credentials: Option[(String, String)] = None): Session = {
			throw new NotImplementedError()
		}
	}
}
