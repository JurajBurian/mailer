package jubu.mailer

import javax.mail.internet.InternetAddress
import javax.mail.{Folder, Store, internet}

import de.saly.javamail.mock2.{MailboxFolder, MockMailbox}
import org.scalatest._

/**
	* @author vaclav.svejcar
	*/
class MailerSpec extends FlatSpec with Matchers {

	val ReceiverAddress = "receiver@test.com"
	val SenderAddress = "sender@test.com"
	val MessageText = "Hello, there"
	val MessageSubject = "Test e-mail"
	val SmtpHost = "localhost"
	val SmtpPort = 25

	"The Mailer" should "send e-mail" in {
		val session = (SmtpAddress(SmtpHost, SmtpPort) :: SessionFactory()).session()

		val content = new Content().text("Hello there!")
		Mailer(session).send(Msg(
			from = new InternetAddress(SenderAddress),
			subject = MessageSubject,
			content = content,
			to = Seq(new internet.InternetAddress(ReceiverAddress)
			)))

		val store: Store = session.getStore("pop3s")
		store.connect(ReceiverAddress, null)
		val inbox: Folder = store.getFolder("INBOX")
		inbox.open(Folder.READ_ONLY)

		inbox.getMessageCount should be(1)
		val first = inbox.getMessage(1)
		first.getSubject should be (MessageSubject)
	}
}
