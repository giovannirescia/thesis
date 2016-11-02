package CustomExceptions

case class MissingTranslationException(message: String = "", cause: Throwable = null) extends Exception(message, cause)
