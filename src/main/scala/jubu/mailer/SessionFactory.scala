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


trait  MailKeys {
	val SMTPHostKey = "mail.smtp.host"
	val SMTPPortKey = "mail.smtp.port"
	val TransportProtocolKey = "mail.transport.protocol"
}


trait Prop extends MailKeys  {

	/**
		*
		* @return sequence of key value pairs
		*/
	def convert(): Seq[(String, _)]

	/**
		*
		* @return sequence of keys in given property
		*/
	def keys() = convert().map(_._1)

}


case class SmtpAddress(host: String, port: Int = 25) extends Prop {
	override def convert() = Seq(SMTPHostKey -> host, SMTPPortKey -> port)
}

case class TransportProtocol(protocol:String = "smtp") extends Prop {
	override def convert() = Seq(TransportProtocolKey->protocol)
}





