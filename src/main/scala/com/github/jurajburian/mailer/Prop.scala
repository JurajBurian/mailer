package com.github.jurajburian.mailer

trait MailKeys {
	val DebugKey = "mail.debug"
	val FromKey = "mail.from"
	val HostKey = "mail.host"
	val MimeAddressStrictKey = "mail.mime.address.strict"
	val SmtpConnectionTimeoutKey = "mail.smtp.connectiontimeout"
	val SmtpFromKey = "mail.smtp.from"
	val SmtpHostKey = "mail.smtp.host"
	val SmtpPortKey = "mail.smtp.port"
	val SmtpUserKey = "mail.smtp.user"
	val SmtpStartTlsEnableKey = "mail.smtp.starttls.enable"
	val SmtpStartTlsRequiredKey = "mail.smtp.starttls.required"
	val SmtpTimeoutKey = "mail.smtp.timeout"
	val StoreProtocolKey = "mail.store.protocol"
	val TransportProtocolKey = "mail.transport.protocol"
	val UserKey = "mail.user"

}

/**
	* Represents the set of one or more ''JavaMail'' properties.
	*/
trait Prop extends MailKeys {

	/**
		* Converts the set of properties into [[scala.Seq]].
		*
		* @return sequence of key value pairs
		*/
	def convert: Seq[(String, _)]

	/**
		* Returns the sequence of property keys.
		*
		* @return sequence of keys in given property
		*/
	def keys: Seq[String] = convert.map(_._1)

}

/**
	* Represents the combination of properties `mail.smtp.host` and `mail.smtp.form`.
	*
	* @param host The SMTP server to connect to.
	* @param port The SMTP server port to connect to, if the connect() method doesn't explicitly
	*             specify one. Defaults to 25.
	*/
case class SmtpAddress(host: String, port: Int = 25) extends Prop {
	override def convert = Seq(SmtpHostKey -> host, SmtpPortKey -> port)
}

/**
	* Represents the ''JavaMail'' property `mail.smtp.connectiontimeout`.
	*
	* @param timeout Socket connection timeout value in milliseconds. Default is infinite timeout.
	*/
case class SmtpConnectionTimeout(timeout: Int) extends Prop {
	override def convert = Seq(SmtpConnectionTimeoutKey -> timeout)
}

/**
	* Represents the ''JavaMail'' property `mail.smtp.from`.
	*
	* @param from Email address to use for SMTP MAIL command. This sets the envelope return address.
	*             Defaults to msg.getFrom() or InternetAddress.getLocalAddress().
	*/
case class SmtpFrom(from: String) extends Prop {
	override def convert = Seq(SmtpFromKey -> from)
}

/**
	* Represents the ''JavaMail'' property `mail.smtp.user`.
	*
	* @param user The user name to use when connecting to mail servers using the specified protocol.
	*             Overrides the mail.user property.
	*/
case class SmtpUser(user: String) extends Prop {
	override def convert = Seq(SmtpUserKey -> user)
}

/**
	* Represents the ''JavaMail'' property `mail.smtp.timeout`.
	*
	* @param timeout Socket I/O timeout value in milliseconds. Default is infinite timeout.
	*/
case class SmtpTimeout(timeout: Int) extends Prop {
	override def convert = Seq(SmtpTimeoutKey -> timeout)
}

/**
	* Represents the set of ''JavaMail'' properties `mail.smtp.starttls.enable`
	* and `mail.smtp.starttls.required`. If called without specifying the parameters,
	* `mail.smtp.starttls.enable` is set to `true` and `mail.smtp.starttls.required` to `false`.
	*
	* @param enable   If true, enables the use of the STARTTLS command (if supported by the server) to
	*                 switch the connection to a TLS-protected connection before issuing any login
	*                 commands. If not set, defaults to false.
	* @param required If true, requires the use of the STARTTLS command. If the server doesn't
	*                 support the STARTTLS command, or the command fails, the connect method will
	*                 fail. If not set, defaults to false.
	*/
case class SmtpStartTls(enable: Boolean = true, required: Boolean = false) extends Prop {
	override def convert = Seq(
		SmtpStartTlsEnableKey -> enable.toString, SmtpStartTlsRequiredKey -> required.toString)
}

/**
	* Represents the ''JavaMail'' property `mail.transport.protocol`.
	*
	* @param protocol Specifies the default message transport protocol. The Session method
	*                 getTransport() returns a Transport object that implements this protocol.
	*                 By default the first Transport provider in the configuration files is returned.
	*/
case class TransportProtocol(protocol: String = "smtp") extends Prop {
	override def convert = Seq(TransportProtocolKey -> protocol)
}

/**
	* Enables the debug mode.
	*
	* @param debug The initial debug mode. Default is false.
	*/
case class Debug(debug: Boolean = false) extends Prop {
	override def convert = Seq(DebugKey -> debug.toString)
}

/**
	* Represents the ''JavaMail'' property `mail.mime.address.strict`.
	*
	* @param mimeAddressStrict The MimeMessage class uses the InternetAddress method parseHeader to
	*                          parse headers in messages. This property controls the strict flag
	*                          passed to the parseHeader method. The default is true.
	*/
case class MimeAddressStrict(mimeAddressStrict: String) extends Prop {
	override def convert = Seq(MimeAddressStrictKey -> mimeAddressStrict)
}

/**
	* Represents the ''JavaMail'' property `mail.host`.
	*
	* @param host The default host name of the mail server for both Stores and Transports. Used if
	*             the `mail.protocol.host` property (set by [[SmtpAddress]])isn't set.
	*/
case class Host(host: String) extends Prop {
	override def convert = Seq(HostKey -> host)
}

/**
	* Represents the ''JavaMail'' property `mail.store.protocol`.
	*
	* @param protocol Specifies the default message access protocol. The Session method getStore()
	*                 returns a Store object that implements this protocol. By default the first
	*                 Store provider in the configuration files is returned.
	*/
case class StoreProtocol(protocol: String) extends Prop {
	override def convert = Seq(StoreProtocolKey -> protocol)
}

/**
	* Represents the ''JavaMail'' property `mail.user`.
	*
	* @param user The default user name to use when connecting to the mail server. Used if
	*             the mail.protocol.user property isn't set.
	*/
case class User(user: String) extends Prop {
	override def convert = Seq(UserKey -> user)
}



