package io.keepcoding.exercise3

trait UserService {

  protected val users: Seq[User]

  protected val phoneLines: Seq[PhoneLine]

  def getAllUsers(): Seq[User] = users

  def getAllIds(): Seq[String] =
    users.map { case User(id, _, _, _) => id }
  //users.map(u => u.id)

  def getAllIdsWithName(): Map[String, String] =
    users.map { case User(id, name, _, _) => (id, name) }.toMap

  def getActiveUsers(): Seq[User] =
    users.filter(_.active)

  def findUserById(id: String): Option[User] = users.find(_.id == id)

  def joinUserAndPhoneLines(): Seq[PhoneLineWithUser] =
    users.flatMap{ user => //Se hace flatMap para que aplane la lista
      phoneLines.filter(pL => pL.userId == user.id).map{ pL =>
        PhoneLineWithUser(user.id, user.name, user.age, user.active, pL.phoneNumber)
      }
    }
}
