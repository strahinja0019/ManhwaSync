package com.strahinja0019.manhwasync.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _manhwaDao: Lazy<ManhwaDao> = lazy {
    ManhwaDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "dd1f197bc06857c0b51bfa219a62e72f", "fabdae57fcea6b9f8f3bada2a2f31dba") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `saved_manhwas` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `coverUrl` TEXT NOT NULL, `localCoverPath` TEXT, `chapterCount` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'dd1f197bc06857c0b51bfa219a62e72f')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `saved_manhwas`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsSavedManhwas: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSavedManhwas.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedManhwas.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedManhwas.put("coverUrl", TableInfo.Column("coverUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedManhwas.put("localCoverPath", TableInfo.Column("localCoverPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSavedManhwas.put("chapterCount", TableInfo.Column("chapterCount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSavedManhwas: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesSavedManhwas: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoSavedManhwas: TableInfo = TableInfo("saved_manhwas", _columnsSavedManhwas, _foreignKeysSavedManhwas, _indicesSavedManhwas)
        val _existingSavedManhwas: TableInfo = read(connection, "saved_manhwas")
        if (!_infoSavedManhwas.equals(_existingSavedManhwas)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |saved_manhwas(com.strahinja0019.manhwasync.database.ManhwaEntity).
              | Expected:
              |""".trimMargin() + _infoSavedManhwas + """
              |
              | Found:
              |""".trimMargin() + _existingSavedManhwas)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "saved_manhwas")
  }

  public override fun clearAllTables() {
    super.performClear(false, "saved_manhwas")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ManhwaDao::class, ManhwaDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun manhwaDao(): ManhwaDao = _manhwaDao.value
}
