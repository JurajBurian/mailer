package jubu.mailer

import java.util.Properties
import javax.mail.{Authenticator, PasswordAuthentication, Session}

import scala.annotation.tailrec

/**
	* @author jubu
	*/
trait Props {
	def append(p: Prop): Props

	def ::(p: Prop): Props = append(p)

	def +(p: Prop): Props = ::(p)

	/**
		*
		* @param credentials (username,password) pair
		* @return Plain mail Session
		*/
	def session(credentials: Option[(String, String)] = None): Session
}

object Props {
	def apply() = new Props {
		override def append(p: Prop): Props = {
			case class impl(prev: impl) extends Props {
				val prop = p

				override def append(p: Prop): Props = new impl(this)

				def properties(): Properties = {
					@tailrec
					def walk(prev: impl, properties: Properties = new Properties()): Properties = {
						prop.convert().map { p => properties.setProperty(p._1, p._2.toString) }
						if (prev == null) properties else walk(prev, properties)
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


trait Prop {
	/**
		*
		* @return sequence of key value pairs
		*/
	def convert(): Seq[(String, _)]
}

case class SmtpAddress(host: String, port: Int) extends Prop {
	override def convert() = Seq("mail.smtp.host" -> host, "mail.smtp.port" -> port)
}


