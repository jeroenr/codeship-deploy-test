package nl.weeronline.hello.authentication

case class AuthInfo(
  authKey: String,
  userId: String,
  permissions: List[String],
  groupIds: List[String],
  expirationDate: Option[Long]
)
