package com.github.jurajburian.mailer

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
}

/**
  * General ''RFC 822 header'', allowing to create any e-mail message header, which is not directly
  * supported by the ''Mailer's'' API.
  *
  * @param name  header name
  * @param value header value
  */
case class CustomHeader(name: String, value: String) extends MessageHeader
