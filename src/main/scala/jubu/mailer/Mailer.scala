package jubu.mailer

import java.io.File
import javax.activation.{FileDataSource, DataHandler}
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import javax.mail.util.ByteArrayDataSource


/**
	* Represents the content of the e-mail message, composed of individual `MimeBodyPart` instances.
	* For easier use, helper methods to add specific content are available, such as `html()` for
	* adding ''HTML'' or `attachFile()` to add file attachment.
	*
	* @param parts parts of the message content (represented by `MimeBodyPart` instances)
	*/
case class Content(parts: MimeBodyPart*) {

	def append(parts: MimeBodyPart*) = Content((this.parts ++ parts): _*)

	def text(text: String, charset: String = "UTF-8", subtype: String = "plain"): Content = {
		val part = new MimeBodyPart()
		part.setText(text, charset, subtype)
		append(part)
	}

	def html(html: String, charset: String = "UTF-8"): Content = {
		val part = new MimeBodyPart()
		part.setText(html, charset, "html")
		append(part)
	}

	def attachFile(file: File, name: String = null): Content = {
		val part = new MimeBodyPart()
		part.setDataHandler(new DataHandler(new FileDataSource(file)))
		part.setFileName(Option(name).getOrElse(file.getName))
		append(part)
	}

	def attachBytes(bytes: Array[Byte], name: String, mimeType: String): Content = {
		val part = new MimeBodyPart()
		part.setDataHandler(new DataHandler(new ByteArrayDataSource(bytes, mimeType)))
		part.setFileName(name)
		append(part)
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
	@throws[MessagingException]
	def connect(): Mailer

	@throws[MessagingException]
	def send(msg: Msg): Mailer

	@throws[MessagingException]
	def close(): Mailer
}


/**
	* @author jubu
	*/
object Mailer extends MailKeys {
	def apply(session: Session, transport: Option[Transport] = None) = new Mailer {


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
			val message = new MimeMessage(session)
			msg.to.map(message.addRecipient(Message.RecipientType.TO, _))
			msg.cc.map(message.addRecipient(Message.RecipientType.CC, _))
			msg.bcc.map(message.addRecipient(Message.RecipientType.BCC, _))
			message.setSubject(msg.subject)
			message.setFrom(msg.from)
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
