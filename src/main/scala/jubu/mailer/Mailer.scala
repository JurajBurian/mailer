package jubu.mailer

import javax.mail._
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}


/**
	*
	* @param parts
	*/
case class Content(parts: MimeBodyPart*) {

	def append(parts: MimeBodyPart*) = Content((this.parts ++ parts): _*)

	def text(text: String, charset: String = "UTF-8", subtype: String = "plain") = {
		val part = new MimeBodyPart()
		part.setText(text, charset, subtype)
		append(part)
	}

	def html(html: String, charset: String = "UTF-8") = {
		val part = new MimeBodyPart()
		part.setText(html, charset, "html")
		append(part)
		part
	}

	@throws[MessagingException]
	def apply() = {
		val prts = parts
		new MimeMultipart() {
			prts.foreach(addBodyPart(_))
		}
	}
}

case class Msg(from: InternetAddress,
							 subject: String,
							 content: Content,
							 to: Seq[InternetAddress] = Seq.empty[InternetAddress],
							 cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
							 bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
							 replyTo: Option[InternetAddress] = None,
							 replyToAll: Option[Boolean] = None) {

}

trait Mailer {
	def connect(): Mailer

	def send(msg: Msg): Mailer

	def close(): Mailer
}


/**
	* @author jubu
	*/
object Mailer extends MailKeys {
	def build(session: Session, transport: Option[Transport] = None) = new Mailer {


		val trt = transport match {
			case None => if (session.getProperty(TransportProtocolKey) == null) {
				session.getTransport("smtp")
			} else {
				session.getTransport
			}
			case Some(t) => t
		}

		@throws[MessagingException]
		override def connect(): Mailer = {
			if (!trt.isConnected) {
				trt.connect()
			}
			this
		}

		@throws[MessagingException]
		override def send(msg: Msg): Mailer = {
			connect()
			msg.content.parts
			val message = new MimeMessage(session)
			msg.to.map(message.addRecipient(Message.RecipientType.TO, _))
			msg.cc.map(message.addRecipient(Message.RecipientType.CC, _))
			msg.bcc.map(message.addRecipient(Message.RecipientType.BCC, _))
			message.setContent(new MimeMultipart() {
				msg.content.parts.foreach(addBodyPart(_))
			})
			trt.sendMessage(message, message.getAllRecipients)
			this
		}

		@throws[MessagingException]
		override def close(): Mailer = {
			if (trt.isConnected) {
				trt.close()
			}
			this
		}
	}
}
