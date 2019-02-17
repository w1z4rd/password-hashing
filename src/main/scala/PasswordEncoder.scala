trait PasswordEncoder {
  def encode(raw: Array[Char]): Password
  def validate(raw: Array[Char], saved: Password): Boolean
}
