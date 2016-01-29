package jubu.mailer

import java.nio.charset.Charset
import javax.mail.internet.{MimeBodyPart, InternetAddress}
import javax.mail.{MessagingException, Transport, Session}



case class Content(parts:MimeBodyPart*) {
	def append(parts:MimeBodyPart*) = Content((this.parts ++ parts):_*)
	def text(text: String, charset: String = "UTF-8", subtype: String = "plain") = {
		val part = new MimeBodyPart()
		part.setText(text, charset, subtype)
		append(part)
	}
}

case class Msg(from:InternetAddress,
               subject: String,
               content:Content,
               to: Seq[InternetAddress] = Seq.empty[InternetAddress],
               cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
               bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
               replyTo: Option[InternetAddress] = None,
               replyToAll: Option[Boolean] = None,
               headers:Props = Props()) {

}

trait Mailer {
	def connect():Mailer
	def send(msg:Msg):Mailer
	def close():Mailer
}


/**
	* @author jubu
	*/
object Mailer {
	def build(session:Session, transport:Option[Transport]) = new Mailer {

		val trt = transport match {
			case None=> null
			case Some(t) => t
		}

		@throws[MessagingException]
		override def connect(): Mailer = {
			if(!trt.isConnected) {
				trt.connect()
			}
			this
		}

		@throws[MessagingException]
		override def send(msg: Msg): Mailer = {
			connect()
		}

		@throws[MessagingException]
		override def close(): Mailer = {
			if(trt.isConnected) {
				trt.close()
			}
			this
		}
	}
}
