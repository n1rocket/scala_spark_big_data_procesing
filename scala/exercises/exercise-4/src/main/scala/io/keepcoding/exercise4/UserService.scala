package io.keepcoding.exercise4

trait UserService {

  protected val users: Seq[User]

  protected val phoneLines: Seq[PhoneLine]

  def joinUserAndPhoneLines(): Seq[PhoneLineWithUser] = users.flatMap { case User(id, name, age, active) =>
    phoneLines.filter(_.userId == id)
      .map { case PhoneLine(_, phoneNumber) =>
        PhoneLineWithUser(id, name, age, active, phoneNumber)
      }
  }

  def joinUserAndPhoneLinesFun(): Seq[PhoneLineWithUser] = ???
}
