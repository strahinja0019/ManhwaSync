package com.strahinja0019.manhwasync.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ManhwaDao_Impl(
  __db: RoomDatabase,
) : ManhwaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfManhwaEntity: EntityInsertAdapter<ManhwaEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfManhwaEntity = object : EntityInsertAdapter<ManhwaEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `saved_manhwas` (`id`,`title`,`coverUrl`,`localCoverPath`,`chapterCount`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ManhwaEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.coverUrl)
        val _tmpLocalCoverPath: String? = entity.localCoverPath
        if (_tmpLocalCoverPath == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLocalCoverPath)
        }
        val _tmpChapterCount: Int? = entity.chapterCount
        if (_tmpChapterCount == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpChapterCount.toLong())
        }
      }
    }
  }

  public override suspend fun insertManhwa(manhwa: ManhwaEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfManhwaEntity.insert(_connection, manhwa)
  }

  public override fun getAllSavedManhwas(): Flow<List<ManhwaEntity>> {
    val _sql: String = "SELECT * FROM saved_manhwas"
    return createFlow(__db, false, arrayOf("saved_manhwas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfLocalCoverPath: Int = getColumnIndexOrThrow(_stmt, "localCoverPath")
        val _columnIndexOfChapterCount: Int = getColumnIndexOrThrow(_stmt, "chapterCount")
        val _result: MutableList<ManhwaEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ManhwaEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpLocalCoverPath: String?
          if (_stmt.isNull(_columnIndexOfLocalCoverPath)) {
            _tmpLocalCoverPath = null
          } else {
            _tmpLocalCoverPath = _stmt.getText(_columnIndexOfLocalCoverPath)
          }
          val _tmpChapterCount: Int?
          if (_stmt.isNull(_columnIndexOfChapterCount)) {
            _tmpChapterCount = null
          } else {
            _tmpChapterCount = _stmt.getLong(_columnIndexOfChapterCount).toInt()
          }
          _item = ManhwaEntity(_tmpId,_tmpTitle,_tmpCoverUrl,_tmpLocalCoverPath,_tmpChapterCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRandomManhwa(): ManhwaEntity? {
    val _sql: String = "SELECT * FROM saved_manhwas ORDER BY RANDOM() LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfCoverUrl: Int = getColumnIndexOrThrow(_stmt, "coverUrl")
        val _columnIndexOfLocalCoverPath: Int = getColumnIndexOrThrow(_stmt, "localCoverPath")
        val _columnIndexOfChapterCount: Int = getColumnIndexOrThrow(_stmt, "chapterCount")
        val _result: ManhwaEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpCoverUrl: String
          _tmpCoverUrl = _stmt.getText(_columnIndexOfCoverUrl)
          val _tmpLocalCoverPath: String?
          if (_stmt.isNull(_columnIndexOfLocalCoverPath)) {
            _tmpLocalCoverPath = null
          } else {
            _tmpLocalCoverPath = _stmt.getText(_columnIndexOfLocalCoverPath)
          }
          val _tmpChapterCount: Int?
          if (_stmt.isNull(_columnIndexOfChapterCount)) {
            _tmpChapterCount = null
          } else {
            _tmpChapterCount = _stmt.getLong(_columnIndexOfChapterCount).toInt()
          }
          _result = ManhwaEntity(_tmpId,_tmpTitle,_tmpCoverUrl,_tmpLocalCoverPath,_tmpChapterCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteManhwaById(manhwaId: String) {
    val _sql: String = "DELETE FROM saved_manhwas WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, manhwaId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
