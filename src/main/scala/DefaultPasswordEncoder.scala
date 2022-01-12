import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import DefaultPasswordEncoder._

sealed class DefaultPasswordEncoder extends PasswordEncoder {

  def encode(password: Array[Char]): Password = {
    SALT_GENERATOR setSeed System.currentTimeMillis()
    val salt: Array[Byte] = new Array[Byte](SALT_BYTE_SIZE)
    SALT_GENERATOR nextBytes salt
    val hash: Array[Byte] =
      pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE)

    Password(toHex(hash), toHex(salt), PBKDF2_ITERATIONS, HASH_BYTE_SIZE)
  }

  def validate(rawPassword: Array[Char], savedPassword: Password): Boolean = {
    val savedPasswordHash = fromHex(savedPassword.hash)
    val rawPasswordHash = pbkdf2(rawPassword,
                                 fromHex(savedPassword.salt),
                                 savedPassword.iterations,
                                 savedPassword.hashSize)

    slowEquals(savedPasswordHash, rawPasswordHash)
  }

  private def pbkdf2(password: Array[Char],
                     salt: Array[Byte],
                     iterations: Int,
                     hashSize: Int): Array[Byte] = {
    val specification: PBEKeySpec =
      new PBEKeySpec(password, salt, iterations, hashSize * 8)
    val secretKeyFactory
      : SecretKeyFactory = SecretKeyFactory getInstance PBKDF2_ALGORITHM
    val encoded = (secretKeyFactory generateSecret specification) getEncoded

    whipe(password)

    encoded
  }

}

object DefaultPasswordEncoder {

  private val SALT_BYTE_SIZE: Short = 32;
  private val HASH_BYTE_SIZE: Short = 30;
  private val PBKDF2_ITERATIONS: Int = 19973;
  private val PBKDF2_ALGORITHM: String = "PBKDF2WithHmacSHA384";
  private val SALT_GENERATOR: SecureRandom =
    SecureRandom.getInstance("SHA1PRNG");

  private def toHex(array: Array[Byte]): String = {
    Base64.getEncoder.encodeToString(array)
  }

  private def fromHex(hex: String): Array[Byte] = {
    Base64.getDecoder.decode(hex)
  }

  private def slowEquals(hash: Array[Byte], testHash: Array[Byte]): Boolean = {
    var diff: Int = hash.length ^ testHash.length
    for {
      i <- 0 until hash.length
      if i < testHash.length
    } diff |= hash(i) ^ testHash(i)

    diff == 0
  }

  private def whipe(password: Array[Char]): Unit = {
    for (i <- 0 until password.length) {
      password(i) = '*'
    }
  }

}
