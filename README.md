# Mailer

![alt tag](/doc/logo.png?raw=true)
A thin wrapper over _JavaMail_ library written in the Scala language. 
`Mailer`'s aim is to be used in situations when it is necessary to send multiple mails efficiently. `Mailer` achieves this by creating a single instance of `javax.mail.Session`, getting and opening a javax.mail.Transport instance, and sending a bulk of emails through it.

There is actually an existing _JavaMail_ e-mail sender library written in _Scala_, called [Courier](https://github.com/softprops/courier). The main motivation of creating another _JavaMail_ wrapper was to solve the several issues of the existing _Courier_ library using following improvements:

1. __Reuses the [Transport](https://javamail.java.net/nonav/docs/api/javax/mail/Transport.html) instance__ - _Courier_ library creates new [Transport](https://javamail.java.net/nonav/docs/api/javax/mail/Transport.html) instance each time the message is sent. Since this may cause performance problems when sending a bulk of e-mails, _Mailer_ keeps the single instance of _Transport_ opened until the connection is explicitly closed.
2. __Does not force asynchronous sending using the  [Future](http://www.scala-lang.org/api/2.11.7/index.html#scala.concurrent.Future$)__ - _Courier_ library works in asynchronous manner using the [Future](http://www.scala-lang.org/api/2.11.7/index.html#scala.concurrent.Future$) and there is no way sending e-mail synchronously. _Mailer_, on the other hand, keeps the decision about synchronicity on the user, so it can be user for example very effectively in combination with the [Akka Actor framework](http://akka.io).

_Remark:_ Multiple threads can use a Session. Since a Transport represents a connection to a mail server, and only a single thread can use the connection at a time, a Transport will synchronize access from multiple threads to maintain thread safety, but you'll really only want to use it from a single thread.

## API documentation
_ScalaDoc_ documentation is available online for the following _Mailer_ versions. _Up-to-date_ documentation for the most actual version can be displayed anytime [here](http://jurajburian.github.io/mailer/api/current/#com.github.jurajburian.mailer.package).

* [version 1.0.x](http://jurajburian.github.io/mailer/api/1.0.x/#com.github.jurajburian.mailer.package)
* [version 1.1.x](http://jurajburian.github.io/mailer/api/1.1.x/#com.github.jurajburian.mailer.package)

## Install
Mailer is available for Scala 2.10 and 2.11
To get started with SBT, add dependency to your build.sbt file:
```Scala
libraryDependencies += "com.github.jurajburian" %% "mailer" % "1.1.0" withSources
```
## Usage
### 1/ Build plain Java Mail Session:  
```Scala
import com.github.jurajburian.mailer._
val session = (SmtpAddress("smtp.gmail.com", 587) :: SessionFactory()).session(Some("user@gmail.com"-> "password"))
//or for example:
val session2 = (SessionFactory() + (SmtpAddress("smtp.gmail.com", 587)).session()
```
One can use more properties (instance of `Prop`) like `SmtpTimeout` concatenated by `::` or `+` operator. List of all available properties can be found in _ScalaDoc_, as the known subclasses of the [Prop trait](http://jurajburian.github.io/mailer/api/current/#com.github.jurajburian.mailer.Prop).

### 2/ Build `Mailer` instance
```Scala
val mailer = Mailer(session)
```
Use optional parameter `transport: Option[Transport]` in `Mailer.apply` method if is necessary to use different than default transport implementation.

### 3/ Send Mail
```Scala
import javax.mail.internet.InternetAddress
val content = new Content().text("Hello there!")
val msg = Message(
      from = new InternetAddress(SenderAddress),
      subject = "my subject",
      content = content,
      to = Seq(new InternetAddress(ReceiverAddress)))
val mailer = Mailer(session)      
// recomendations: use try       
Try{mailer.send(msg)}
// or  future 
Future{mailer.send(msg)}
```
There is several methods how to create `Content`. If one can't find any appropriate method, `Content` constructor is able to accept sequence of instances: `javax.mail.internet.MimeBodyPart` 
_Remark:_ All methods from the Mailer trait may thrown `javax.mail.MessagingException`.
### 4/ Close Session
`Mailer` "session" should be closed. Call `mailer.close()` or `Try{mailer.close()}` for this purpose.

### Changelog

1. v1.1.0
   * instance of `javax.mail.Transport`, used by the mailer is now accessible via [`Mailer#transport`](http://jurajburian.github.io/mailer/api/1.1.x/index.html#com.github.jurajburian.mailer.Mailer@transport:javax.mail.Transport)
   * `MailerSpec` test suite now properly closes _SMTP session_ after test is finished
2. v1.0.0
   * initial release

### Contributors
* Juraj Burian ([@JurajBurian](https://github.com/JurajBurian))
* Vaclav Svejcar ([@xwinus](https://github.com/xwinus))
* Jan Nad ([@jannad](https://github.com/jannad))
