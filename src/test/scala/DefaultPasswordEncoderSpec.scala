import org.specs2.mutable.Specification
import java.util.UUID

class DefaultPasswordEncoderSpec extends Specification {

  sequential

  val passwordEncoder: PasswordEncoder = new DefaultPasswordEncoder()

  "passwords" should {
    "have different hashes for different salt values" >> {
      val list = List("password", "password", "password", "password", "password", "password", "password", "password",
        "password", "password")

      val passwordsList: List[Password] = list
        .map(_.toCharArray())
        .map(passwordEncoder.encode)
      for (i <- 0 until passwordsList.length) {
        passwordsList.drop(i + 1).foreach(x => {
          if(passwordsList(i).salt.equalsIgnoreCase(x.salt)) passwordsList(i).hash.equalsIgnoreCase(x.hash) must beTrue
          else passwordsList(i).hash.equalsIgnoreCase(x.hash) must beFalse
        })
      }

      ok
    }

    "validate when they are the same" >> {
      val passwordString = "password"
      val password = passwordEncoder.encode(passwordString.toCharArray())

      val adminString = "admin"
      val adminPassword = passwordEncoder.encode(adminString.toCharArray())

      val lightidString = "lightid"
      val lightidPassword = passwordEncoder.encode(lightidString.toCharArray())

      val uuid = UUID.randomUUID
      val uuidPassword = passwordEncoder.encode(uuid.toString.toCharArray())

      passwordEncoder.validate(passwordString.toCharArray(), password) must beTrue
      passwordEncoder.validate(adminString.toCharArray(), adminPassword) must beTrue
      passwordEncoder.validate(lightidString.toCharArray(), lightidPassword) must beTrue
      passwordEncoder.validate(uuid.toString.toCharArray(), uuidPassword) must beTrue

      ok
    }

    "not validate when they are different" >> {
      val passwordString = "password"
      val wrongPasswordString = "wrongPassword"
      val password = passwordEncoder.encode(passwordString.toCharArray())

      val uuid = UUID.randomUUID
      val uuidPassword = passwordEncoder.encode(UUID.randomUUID.toString.toCharArray())

      passwordEncoder.validate(wrongPasswordString.toCharArray(), password) must beFalse
      passwordEncoder.validate(uuid.toString.toCharArray(), uuidPassword) must beFalse

      ok
    }

    "have hash size equal to 30" >> {
      val rawPassword = "password"
      val password = passwordEncoder.encode(rawPassword.toCharArray())

      password.hashSize == 30
      password.hash.size == 30

      ok
    }

    "have salt size equal to 32" >> {
      val rawPassword = "password"
      val password = passwordEncoder.encode(rawPassword.toCharArray())

      password.salt.size == 32

      ok
    }

    "be whiped after encode" >> {
      val password = UUID.randomUUID.toString.toCharArray()
      passwordEncoder.encode(password)

      for(i <- 0 until password.length) {
        password(i) == '*' must beTrue
      } 

      ok
    }

    "be whiped after validate" >> {
      val pass = passwordEncoder.encode("random".toCharArray())
      val password = UUID.randomUUID.toString.toCharArray()

      passwordEncoder.validate(password, pass)

      for(i <- 0 until password.length) {
        password(i) == '*' must beTrue
      } 

      ok
    }

  }
}
