# Mailer
![alt tag](/doc/logo.png?raw=true)
A thin wrapper over JavaMail library written in the Scala language. 
`Mailer`'s aim is to be used in situations when it is necessary to send multiple mails efficiently. `Mailer` achieves this by creating a single instance of `javax.mail.Session`, getting and opening a javax.mail.Transport instance, and sending a bulk of emails through it.
## Install
// TODO
## Usage
###At first, build plain Java Mail Session:  
```Scala
import com.github.jurajburian.mailer._
val session = (SmtpAddress("smtp.gmail.com", 587) :: SessionFactory()).session(Some("user@gmail.com"-> "password"))
//or for example:
val session2 = (SessionFactory() + (SmtpAddress("smtp.gmail.com", 587)).session()
```
One can use more properties (instance of `Prop`) like `SmtpTimeout` concatenated by `::` or `+` operator  
###Build `Mailer` instance
```Scala
val mailer = Mailer(session)
```
Use optional parameter `transport: Option[Transport]` in `Mailer.apply` method if is necessary to use different than default transport implementation.
###Send Mail
```Scala
import javax.mail.internet.InternetAddress
val content = new Content().text("Hello there!")
val msg = Msg(
      from = new InternetAddress(SenderAddress),
      subject = "my subject",
      content = content,
      to = Seq(new InternetAddress(ReceiverAddress)))
val mailer = Mailer(session)      
// use try       
Try{mailer.send(msg)}
// or  future 
Future{mailer.send(msg)}
```
There is several methods how to create `Content`. If one can't find any appropriate method, `Content` constructor is able to accept sequence of instances: `javax.mail.internet.MimeBodyPart` 
###Close Session
`Mailer` "session" should be closed. Call `mailer.close()` or `Try{mailer.close()}` for this purpose.

###Todo 
1. complete documentation with installation guide
2. Complete `Content` with more "assemble" methods.
3. Complete `Prop` with more concrete properties
4. more tests
5. add link to currier, and describe motivation why we created next wrapper

### Changelog

###Contributors
* Juraj Burian (@JurajBurian)
* Vaclav Svejcar
* Jan Nad