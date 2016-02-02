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

	/**
		* Appends the given part (represented by `MimeBodyPart` instance) to the existing content parts.
		*
		* @param parts content part to append
		* @return instance of the [[jubu.mailer.Content]] class with appended content part
		*/
	def append(parts: MimeBodyPart*) = Content((this.parts ++ parts): _*)

	/**
		* Appends the given string as the text content part (defaults to ''text/plain'').
		*
		* @param text    text to append
		* @param charset charset of the given text (defaults to ''UTF-8'')
		* @param subtype defines subtype of the ''MIME type'' (the part after the slash), defaults
		*                to ''UTF-8''
		* @return instance of the [[jubu.mailer.Content]] class with appended content part
		*/
	def text(text: String, charset: String = "UTF-8", subtype: String = "plain"): Content = {
		val part = new MimeBodyPart()
		part.setText(text, charset, subtype)
		append(part)
	}

	/**
		* Appends the given ''HTML'' string as the new ''HTML'' content part.
		*
		* @param html    ''HTML'' string to append
		* @param charset charset of the given ''HTML'' string (defaults to ''UTF-8'')
		* @return instance of the [[jubu.mailer.Content]] class with appended content part
		*/
	def html(html: String, charset: String = "UTF-8"): Content = {
		val part = new MimeBodyPart()
		part.setText(html, charset, "html")
		append(part)
	}

	/**
		* Appends the given file as the e-mail message attachment.
		*
		* @param file file to attach
		* @param name name of the attachment (optional, defaults to the given file name)
		* @return instance of the [[jubu.mailer.Content]] class with appended content part
		*/
	def attachFile(file: File, name: String = null): Content = {
		val part = new MimeBodyPart()
		part.setDataHandler(new DataHandler(new FileDataSource(file)))
		part.setFileName(Option(name).getOrElse(file.getName))
		append(part)
	}

	/**
		* Appends the given array of bytes as the e-mail message attachment. Useful especially when the
		* original file object is not available, only its array of bytes.
		*
		* @param bytes    array of bytes representing the attachment
		* @param name     name of the attachment
		* @param mimeType ''MIME type'' of the attachment
		* @return instance of the [[jubu.mailer.Content]] class with appended content part
		*/
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

/**
	* Represents the e-mail message itself.
	*
	* @param from       e-mail sender address
	* @param subject    e-mail subject text
	* @param content    e-mail content, represented by the instance of [[jubu.mailer.Content]] class
	* @param to         set of e-mail receiver addresses
	* @param cc         set of e-mail ''carbon copy'' receiver addresses
	* @param bcc        set of e-mail ''blind carbon copy'' receiver addresses
	* @param replyTo    address used to reply this message (optional)
	* @param replyToAll whether the new message will be addressed to all recipients of this message
	*/
case class Msg(from: InternetAddress,
							 subject: String,
							 content: Content,
							 to: Seq[InternetAddress] = Seq.empty[InternetAddress],
							 cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
							 bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
							 replyTo: Option[InternetAddress] = None,
							 replyToAll: Option[Boolean] = None) {

}

/**
	* Represents the ''Mailer'' itself, with methods for opening/closing the connection and sending
	* the message ([[jubu.mailer.Msg]])
	*/
trait Mailer {

	/**
		* Creates new transport connection.
		*
		* @throws MessagingException thrown when a problem with creating the new connection occurs
		* @return instance of the [[jubu.mailer.Mailer]] itself
		*/
	@throws[MessagingException]
	def connect(): Mailer

	/**
		* Sends the given message.
		*
		* @param msg message to send
		* @throws MessagingException thrown when a problem with sending the message occurs
		* @return instance of the [[jubu.mailer.Mailer]] itself
		*/
	@throws[MessagingException]
	def send(msg: Msg): Mailer

	/**
		* Closes the previously opened transport connection.
		*
		* @throws MessagingException throw when a problem with closing the connection occurs
		* @return instance of the [[jubu.mailer.Mailer]] itself
		*/
	@throws[MessagingException]
	def close(): Mailer
}


/**
	* ''Mailer'' object providing default operations to handle the transport connection and send the
	* e-mail message.
	*
	* @author jubu
	*/
object Mailer extends MailKeys {
	/**
		* Sets the ''JavaMail'' session to the ''Mailer'' and returns the instance ready to send e-mail
		* messages. Optionally, transport method can be explicitly specified.
		*
		* @param session   ''JavaMail'' session
		* @param transport transport method (optional)
		* @return ''Mailer'' instance
		*/
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
