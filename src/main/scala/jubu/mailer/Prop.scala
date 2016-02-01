package jubu.mailer

trait MailKeys {
	val SmtpConnectionTimeoutKey = "mail.smtp.connectiontimeout"
	val SmtpFromKey = "mail.smtp.from"
	val SmtpHostKey = "mail.smtp.host"
	val SmtpPortKey = "mail.smtp.port"
	val SmtpTimeoutKey = ""
	val TransportProtocolKey = "mail.transport.protocol"
}

trait Prop extends MailKeys {

	/**
		*
		* @return sequence of key value pairs
		*/
	def convert: Seq[(String, _)]

	/**
		*
		* @return sequence of keys in given property
		*/
	def keys = convert.map(_._1)

}


case class SmtpAddress(host: String, port: Int = 25) extends Prop {
	override def convert = Seq(SmtpHostKey -> host, SmtpPortKey -> port)
}

case class SmtpConnectionTimeout(timeout: Int) extends Prop {
	override def convert = Seq(SmtpConnectionTimeoutKey -> timeout)
}

case class SmtpFrom(from: String) extends Prop {
	override def convert = Seq(SmtpFromKey -> from)
}

case class SmtpTimeout(timeout: Int) extends Prop {
	override def convert = Seq(SmtpTimeoutKey -> timeout)
}

case class TransportProtocol(protocol: String = "smtp") extends Prop {
	override def convert = Seq(TransportProtocolKey -> protocol)
}