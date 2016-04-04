package com.github.jurajburian.mailer

import javax.mail.internet.ParameterList

case class HeaderParam(name: String, value: String)

/**
	* Represents ''RFC 822 header'' of the e-mail message. Note that ''RFC 822'' headers must contain
	* only US-ASCII characters, so a header that contains non US-ASCII characters must have been
	* encoded by the caller as per the rules of ''RFC 2047''.
	*
	* @author vaclav.svejcar
	*/
trait MessageHeader {

	/**
		* Represents the name of the header.
		*
		* @return header name
		*/
	def name: String

	/**
		* Represents the value of the header.
		*
		* @return header value
		*/
	def value: String

	/**
		* Encodes the value together with the given map of ''MIME'' parameters, according to the
		* [[http://www.ietf.org/rfc/rfc2231.txt RFC 2331]] specification.
		*
		* @param value  header value
		* @param params header ''MIME'' properties
		* @return encoded value with ''MIME'' properties
		* @see http://www.ietf.org/rfc/rfc2231.txt
		*/
	protected def valueWithParams(value: String, params: Map[String, String]): String = {
		val parameterList: ParameterList = new ParameterList()
		params.foreach(param => parameterList.set(param._1, param._2))

		value + parameterList.toString(name.length + 2)
	}
}

/**
	* Represents the `Content-Disposition` ''RFC 822'' message header, with the given header value
	* and optional header parameters.
	*
	* @param disposition value of the header
	* @param params      parameters of the header
	* @see http://www.iana.org/assignments/cont-disp/cont-disp.xhtml
	*/
case class ContentDisposition(disposition: String,
															params: Map[String, String] = Map.empty[String, String])
	extends MessageHeader {

	override def name: String = "Content-Disposition"

	override def value: String = valueWithParams(disposition, params)
}

/**
	* General ''RFC 822 header'', allowing to create any e-mail message header, which is not directly
	* supported by the ''Mailer's'' API.
	*
	* @param name  header name
	* @param value header value
	*/
case class CustomHeader(name: String, value: String) extends MessageHeader
