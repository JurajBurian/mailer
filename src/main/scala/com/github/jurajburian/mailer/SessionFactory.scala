package com.github.jurajburian.mailer

import java.util.Properties
import javax.mail.{PasswordAuthentication, Authenticator, Session}

import scala.annotation.tailrec


/**
	* Represents the factory, used to create ''JavaMail'' session with selected properties.
	*
	* @author jubu
	*/
trait SessionFactory {
	/**
		* Adds the given ''JavaMail'' property to the set of existing properties.
		*
		* @param p property to add
		* @return instance of [[com.github.jurajburian.mailer.SessionFactory]] itself
		*/
	def append(p: Prop): SessionFactory

	/**
		* Adds the given ''JavaMail'' property to the set of existing properties (used usually as the
		* prepend operator).
		*
		* @param p property to add
		* @return instance of [[com.github.jurajburian.mailer.SessionFactory]] itself
		*/
	def ::(p: Prop): SessionFactory = append(p)

	/**
		* Adds the given ''JavaMail'' property to the set of existing properties (used usually as the
		* append operator).
		*
		* @param p property to add
		* @return instance of [[com.github.jurajburian.mailer.SessionFactory]] itself
		*/
	def +(p: Prop): SessionFactory = ::(p)

	/**
		* Returns the created ''JavaMail'' session, optionally credentials can be provided if required.
		*
		* @param credentials credentials, represented as the pair of (username, password), optional
		* @return Plain mail Session
		*/
	def session(credentials: Option[(String, String)] = None): Session
}

/**
	* Provides set of operations needed to create [[com.github.jurajburian.mailer.SessionFactory]] instance.
	*/
object SessionFactory {
	def apply() = new SessionFactory {
		override def append(p: Prop): SessionFactory = {
			@tailrec
			def walk(p: impl, properties: Properties = new Properties()): Properties = {
				p.prop.convert.map { p => properties.setProperty(p._1, p._2.toString) }
				if (p.prev == null) {
					properties
				} else {
					walk(p.prev, properties)
				}
			}
			case class impl(prev: impl, val prop: Prop) extends SessionFactory {
				override def append(p: Prop): SessionFactory = new impl(this, p)

				def properties(): Properties = {
					walk(this)
				}

				override def session(credentials: Option[(String, String)] = None): Session = {
					val ps = properties()
					credentials match {
						case None => {
							Session.getInstance(ps)
						}
						case Some((u, p)) => {
							ps.put("mail.smtp.auth", true.toString)
							Session.getInstance(ps, new Authenticator {
								protected override def getPasswordAuthentication() = new PasswordAuthentication(u, p)
							})
						}
					}
				}
			}
			impl(null, p)
		}

		override def session(credentials: Option[(String, String)] = None): Session = {
			throw new NotImplementedError()
		}
	}
}
