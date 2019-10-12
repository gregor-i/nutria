package repo

import anorm._
import javax.inject.{Inject, Singleton}
import play.api.db.Database

@Singleton()
class FractalImageRepo @Inject()(db: Database) {

  def getHash(id: String): Option[String] =
    db.withConnection { implicit con =>
      SQL"""SELECT hash
            FROM fractal_images
            WHERE fractal_id = $id"""
        .as(SqlParser.scalar[String].singleOpt)
    }

  def getImage(id: String): Option[Array[Byte]] =
    db.withConnection { implicit con =>
      SQL"""SELECT image
            FROM fractal_images
            WHERE fractal_id = $id"""
        .as(SqlParser.scalar[Array[Byte]].singleOpt)
    }

  def save(id: String, hash: String, image: Array[Byte]): Unit =
    db.withConnection { implicit con =>
      SQL"""INSERT INTO fractal_images (fractal_id, hash, image)
            VALUES (${id}, ${hash}, ${image})
            ON CONFLICT (fractal_id) DO UPDATE
            SET image = ${image}, hash = ${hash}
        """.executeUpdate()
    }

  def isDefined(id: String): Boolean =
    db.withConnection { implicit con =>
      SQL"""SELECT 1
            FROM fractal_images
            WHERE fractal_id = $id"""
        .as(SqlParser.scalar[Int].singleOpt).isDefined
    }

  def truncate(): Unit =
    db.withConnection { implicit con =>
      SQL"""TRUNCATE fractal_images"""
        .executeUpdate()
    }
}
