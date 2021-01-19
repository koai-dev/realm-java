package io.realm

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.realm.entities.AllJavaTypes
import io.realm.entities.MixedIndexed
import io.realm.entities.MixedNotIndexed
import io.realm.entities.PrimaryKeyAsString
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


// FIXME: MIXED PARAMETRIZED TESTS FOR INDEXED AND UNINDEXED
@RunWith(AndroidJUnit4::class)
class MixedTests {
    private lateinit var realmConfiguration: RealmConfiguration
    private lateinit var realm: Realm

    @Rule
    @JvmField
    val folder = TemporaryFolder()

    init {
        Realm.init(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun setUp() {
        realmConfiguration = RealmConfiguration
                .Builder(InstrumentationRegistry.getInstrumentation().targetContext)
                .directory(folder.newFolder())
                .schema(MixedNotIndexed::class.java,
                        MixedIndexed::class.java,
                        AllJavaTypes::class.java,
                        PrimaryKeyAsString::class.java)
                .build()

        realm = Realm.getInstance(realmConfiguration)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    // Unmanaged
    @Test
    fun unmanaged_byteValue() {
        val mixed = Mixed.valueOf(10.toByte())

        assertEquals(10, mixed.asByte())
        assertEquals(MixedType.INTEGER, mixed.type)
    }

    @Test
    fun unmanaged_shortValue() {
        val mixed = Mixed.valueOf(10.toShort())

        assertEquals(10, mixed.asShort())
        assertEquals(MixedType.INTEGER, mixed.type)
    }

    @Test
    fun unmanaged_integerValue() {
        val mixed = Mixed.valueOf(10.toInt())

        assertEquals(10, mixed.asInteger())
        assertEquals(MixedType.INTEGER, mixed.type)
        assertEquals(MixedType.INTEGER.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_longValue() {
        val mixed = Mixed.valueOf(10.toLong())

        assertEquals(10, mixed.asLong())
        assertEquals(MixedType.INTEGER, mixed.type)
    }

    @Test
    fun unmanaged_booleanValue() {
        val mixed = Mixed.valueOf(true)

        assertEquals(true, mixed.asBoolean())
        assertEquals(MixedType.BOOLEAN, mixed.type)
        assertEquals(MixedType.BOOLEAN.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_stringValue() {
        val mixed = Mixed.valueOf("hello world")

        assertEquals("hello world", mixed.asString())
        assertEquals(MixedType.STRING, mixed.type)
        assertEquals(MixedType.STRING.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_binaryValue() {
        val mixed = Mixed.valueOf(byteArrayOf(0, 1, 0))

        assertTrue(Arrays.equals(byteArrayOf(0, 1, 0), mixed.asBinary()))
        assertEquals(MixedType.BINARY, mixed.type)
        assertEquals(MixedType.BINARY.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_dateValue() {
        val mixed = Mixed.valueOf(Date(10))

        assertEquals(Date(10), mixed.asDate())
        assertEquals(MixedType.DATE, mixed.type)
        assertEquals(MixedType.DATE.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_decimal128Value() {
        val mixed = Mixed.valueOf(Decimal128.fromIEEE754BIDEncoding(10, 10))

        assertEquals(Decimal128.fromIEEE754BIDEncoding(10, 10), mixed.asDecimal128())
        assertEquals(MixedType.DECIMAL128, mixed.type)
        assertEquals(MixedType.DECIMAL128.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_doubleValue() {
        val mixed = Mixed.valueOf(10.0)

        assertEquals(10.0, mixed.asDouble())
        assertEquals(MixedType.DOUBLE, mixed.type)
        assertEquals(MixedType.DOUBLE.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_floatValue() {
        val mixed = Mixed.valueOf(10.0f)

        assertEquals(10.0f, mixed.asFloat())
        assertEquals(MixedType.FLOAT, mixed.type)
        assertEquals(MixedType.FLOAT.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_objectIdValue() {
        val mixed = Mixed.valueOf(ObjectId(TestHelper.generateObjectIdHexString(0)))

        assertEquals(ObjectId(TestHelper.generateObjectIdHexString(0)), mixed.asObjectId())
        assertEquals(MixedType.OBJECT_ID, mixed.type)
        assertEquals(MixedType.OBJECT_ID.typedClass, mixed.valueClass)
    }

    @Test
    fun unmanaged_realmModel() {
        val obj = MixedNotIndexed()
        val mixed = Mixed.valueOf(obj)

        assertEquals(obj, mixed.asRealmModel(MixedNotIndexed::class.java))
        assertEquals(MixedType.OBJECT, mixed.type)
        assertEquals(MixedNotIndexed::class.simpleName, mixed.valueClass?.simpleName)
    }

    @Test
    fun unmanaged_UUIDValue() {
        val mixed = Mixed.valueOf(UUID.fromString(TestHelper.generateUUIDString(0)))

        assertEquals(UUID.fromString(TestHelper.generateUUIDString(0)), mixed.asUUID())
        assertEquals(MixedType.UUID, mixed.type)
    }

    @Test
    fun unmanaged_null() {
        val aLong: Boolean? = null

        val mixed = Mixed.valueOf(aLong)

        assertTrue(mixed.isNull)
        assertEquals(MixedType.NULL, mixed.type)
        assertEquals(null, mixed.valueClass)
    }


    // Managed Tests
    @Test
    fun managed_byteValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10.toByte())
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(10, mixedObject.mixed?.asByte())
        assertEquals(MixedType.INTEGER, mixedObject.mixed?.type)
    }

    @Test
    fun managed_shortValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10.toShort())
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(10, mixedObject.mixed?.asShort())
        assertEquals(MixedType.INTEGER, mixedObject.mixed?.type)
    }

    @Test
    fun managed_integerValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10.toInt())
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(10, mixedObject.mixed?.asInteger())
        assertEquals(MixedType.INTEGER, mixedObject.mixed?.type)
    }

    @Test
    fun managed_longValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10.toLong())
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(10, mixedObject.mixed?.asLong())
        assertEquals(MixedType.INTEGER, mixedObject.mixed?.type)
        assertEquals(MixedType.INTEGER.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_booleanValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(true)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(true, mixedObject.mixed?.asBoolean())
        assertEquals(MixedType.BOOLEAN, mixedObject.mixed?.type)
        assertEquals(MixedType.BOOLEAN.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_stringValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf("hello world")
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals("hello world", mixedObject.mixed?.asString())
        assertEquals(MixedType.STRING, mixedObject.mixed?.type)
        assertEquals(MixedType.STRING.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_binaryValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(byteArrayOf(0, 1, 0))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertTrue(Arrays.equals(byteArrayOf(0, 1, 0), mixedObject.mixed?.asBinary()))
        assertEquals(MixedType.BINARY, mixedObject.mixed?.type)
        assertEquals(MixedType.BINARY.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_dateValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(Date(10))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(Date(10), mixedObject.mixed?.asDate())
        assertEquals(MixedType.DATE, mixedObject.mixed!!.type)
        assertEquals(MixedType.DATE.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_decimal128Value() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(Decimal128(10))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(Decimal128(10), mixedObject.mixed!!.asDecimal128())
        assertEquals(MixedType.DECIMAL128, mixedObject.mixed!!.type)
        assertEquals(MixedType.DECIMAL128.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_doubleValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10.0)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(10.0, mixedObject.mixed!!.asDouble())
        assertEquals(MixedType.DOUBLE, mixedObject.mixed!!.type)
        assertEquals(MixedType.DOUBLE.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_floatValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(10f)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(10f, mixedObject.mixed!!.asFloat())
        assertEquals(MixedType.FLOAT, mixedObject.mixed!!.type)
        assertEquals(MixedType.FLOAT.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_objectIdValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(ObjectId(TestHelper.generateObjectIdHexString(0)))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(ObjectId(TestHelper.generateObjectIdHexString(0)), mixedObject.mixed!!.asObjectId())
        assertEquals(MixedType.OBJECT_ID, mixedObject.mixed!!.type)
        assertEquals(MixedType.OBJECT_ID.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_UUIDValue() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.valueOf(UUID.fromString(TestHelper.generateUUIDString(0)))
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(UUID.fromString(TestHelper.generateUUIDString(0)), mixedObject.mixed!!.asUUID())
        assertEquals(MixedType.UUID, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_null() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = null
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertTrue(mixedObject.mixed!!.isNull)
        assertEquals(MixedType.NULL, mixedObject.mixed!!.type)
        assertEquals(MixedType.NULL.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_linkUnmanagedRealmModel() {
        val key = UUID.randomUUID().toString()

        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            val innerObject = PrimaryKeyAsString(key)

            mixedObject.mixed = Mixed.valueOf(innerObject)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(key, mixedObject.mixed!!.asRealmModel(PrimaryKeyAsString::class.java).name)
        assertEquals(MixedType.OBJECT, mixedObject.mixed!!.type)
        assertEquals(PrimaryKeyAsString::class.simpleName, mixedObject.mixed!!.valueClass?.simpleName)
    }

    @Test
    fun managed_linkManagedRealmModel() {
        val key = UUID.randomUUID().toString()

        realm.executeTransaction {
            val mixedObject = realm.createObject<MixedNotIndexed>()
            val innerObject = realm.createObject<PrimaryKeyAsString>(key)

            mixedObject.mixed = Mixed.valueOf(innerObject)
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertEquals(key, mixedObject.mixed!!.asRealmModel(PrimaryKeyAsString::class.java).name)
        assertEquals(MixedType.OBJECT, mixedObject.mixed!!.type)
        assertEquals(PrimaryKeyAsString::class.simpleName, mixedObject.mixed!!.valueClass?.simpleName)
    }

    @Test
    fun managed_nullMixed() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.nullValue()
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isManaged)
        assertTrue(mixedObject.mixed!!.isNull)
        assertEquals(MixedType.NULL, mixedObject.mixed!!.type)
        assertEquals(MixedType.NULL.typedClass, mixedObject.mixed!!.valueClass)
    }

    @Test
    fun managed_validity() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.nullValue()
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObject!!.isValid)

        realm.executeTransaction {
            mixedObject.deleteFromRealm()
        }

        assertFalse(mixedObject.isValid)
    }

    @Test
    fun managed_frozen() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.nullValue()
        }

        val mixedObject = realm.where<MixedNotIndexed>().findFirst()

        assertFalse(mixedObject!!.isFrozen)
        assertTrue(mixedObject.isValid)
        assertTrue(mixedObject.mixed!!.isNull)
        assertEquals(MixedType.NULL, mixedObject.mixed!!.type)
    }

    @Test
    fun managed_notFrozen() {
        realm.executeTransaction {
            val mixedObject = it.createObject<MixedNotIndexed>()
            mixedObject.mixed = Mixed.nullValue()
        }

        val mixedObjectFrozen = realm.freeze().where<MixedNotIndexed>().findFirst()

        assertTrue(mixedObjectFrozen!!.isFrozen)
        assertTrue(mixedObjectFrozen.isValid)
        assertTrue(mixedObjectFrozen.mixed!!.isNull)
        assertEquals(MixedType.NULL, mixedObjectFrozen.mixed!!.type)
    }

    @Test
    fun managed_listsAddAllTypes() {
        val aString = "a string"
        val byteArray = byteArrayOf(0, 1, 0)
        val date = Date()
        val objectId = ObjectId()
        val decimal128 = Decimal128(1)
        val uuid = UUID.randomUUID()

        realm.executeTransaction {
            val allJavaTypes = it.createObject<AllJavaTypes>(0)

            allJavaTypes.fieldMixedList.add(Mixed.valueOf(true))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(1.toByte()))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(2.toShort()))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(3.toInt()))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(4.toLong()))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(5.toFloat()))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(6.toDouble()))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(aString))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(byteArray))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(date))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(objectId))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(decimal128))
            allJavaTypes.fieldMixedList.add(Mixed.valueOf(uuid))
            allJavaTypes.fieldMixedList.add(Mixed.nullValue())
            allJavaTypes.fieldMixedList.add(null)
        }

        val allJavaTypes = realm.where<AllJavaTypes>().findFirst()

        assertEquals(true, allJavaTypes!!.fieldMixedList[0]!!.asBoolean())
        assertEquals(1, allJavaTypes.fieldMixedList[1]!!.asByte())
        assertEquals(2, allJavaTypes.fieldMixedList[2]!!.asShort())
        assertEquals(3, allJavaTypes.fieldMixedList[3]!!.asInteger())
        assertEquals(4, allJavaTypes.fieldMixedList[4]!!.asLong())
        assertEquals(5.toFloat(), allJavaTypes.fieldMixedList[5]!!.asFloat())
        assertEquals(6.toDouble(), allJavaTypes.fieldMixedList[6]!!.asDouble())
        assertEquals(aString, allJavaTypes.fieldMixedList[7]!!.asString())
        assertTrue(Arrays.equals(byteArray, allJavaTypes.fieldMixedList[8]!!.asBinary()))
        assertEquals(date, allJavaTypes.fieldMixedList[9]!!.asDate())
        assertEquals(objectId, allJavaTypes.fieldMixedList[10]!!.asObjectId())
        assertEquals(decimal128, allJavaTypes.fieldMixedList[11]!!.asDecimal128())
        assertEquals(uuid, allJavaTypes.fieldMixedList[12]!!.asUUID())
        assertTrue(allJavaTypes.fieldMixedList[13]!!.isNull)
        assertTrue(allJavaTypes.fieldMixedList[14]!!.isNull)
    }

    @Test
    fun managed_listsInsertAllTypes() {
        val aString = "a string"
        val byteArray = byteArrayOf(0, 1, 0)
        val date = Date()
        val objectId = ObjectId()
        val decimal128 = Decimal128(1)
        val uuid = UUID.randomUUID()

        realm.executeTransaction {
            val allJavaTypes = it.createObject<AllJavaTypes>(0)

            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(true))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(1.toByte()))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(2.toShort()))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(3.toInt()))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(4.toLong()))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(5.toFloat()))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(6.toDouble()))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(aString))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(byteArray))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(date))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(objectId))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(decimal128))
            allJavaTypes.fieldMixedList.add(0, Mixed.valueOf(uuid))
            allJavaTypes.fieldMixedList.add(0, Mixed.nullValue())
            allJavaTypes.fieldMixedList.add(0, null)
        }

        val allJavaTypes = realm.where<AllJavaTypes>().findFirst()

        assertEquals(true, allJavaTypes!!.fieldMixedList[14]!!.asBoolean())
        assertEquals(1, allJavaTypes.fieldMixedList[13]!!.asByte())
        assertEquals(2, allJavaTypes.fieldMixedList[12]!!.asShort())
        assertEquals(3, allJavaTypes.fieldMixedList[11]!!.asInteger())
        assertEquals(4, allJavaTypes.fieldMixedList[10]!!.asLong())
        assertEquals(5.toFloat(), allJavaTypes.fieldMixedList[9]!!.asFloat())
        assertEquals(6.toDouble(), allJavaTypes.fieldMixedList[8]!!.asDouble())
        assertEquals(aString, allJavaTypes.fieldMixedList[7]!!.asString())
        assertTrue(Arrays.equals(byteArray, allJavaTypes.fieldMixedList[6]!!.asBinary()))
        assertEquals(date, allJavaTypes.fieldMixedList[5]!!.asDate())
        assertEquals(objectId, allJavaTypes.fieldMixedList[4]!!.asObjectId())
        assertEquals(decimal128, allJavaTypes.fieldMixedList[3]!!.asDecimal128())
        assertEquals(uuid, allJavaTypes.fieldMixedList[2]!!.asUUID())
        assertTrue(allJavaTypes.fieldMixedList[1]!!.isNull)
        assertTrue(allJavaTypes.fieldMixedList[0]!!.isNull)
    }

    @Test
    fun managed_listsSetAllTypes() {
        val aString = "a string"
        val byteArray = byteArrayOf(0, 1, 0)
        val date = Date()
        val objectId = ObjectId()
        val decimal128 = Decimal128(1)
        val uuid = UUID.randomUUID()

        realm.executeTransaction {
            val allJavaTypes = it.createObject<AllJavaTypes>(0)

            val initialList = RealmList<Mixed>()
            initialList.addAll(arrayOfNulls(15))
            allJavaTypes.fieldMixedList = initialList

            allJavaTypes.fieldMixedList[0] = Mixed.valueOf(true)
            allJavaTypes.fieldMixedList[1] = Mixed.valueOf(1.toByte())
            allJavaTypes.fieldMixedList[2] = Mixed.valueOf(2.toShort())
            allJavaTypes.fieldMixedList[3] = Mixed.valueOf(3.toInt())
            allJavaTypes.fieldMixedList[4] = Mixed.valueOf(4.toLong())
            allJavaTypes.fieldMixedList[5] = Mixed.valueOf(5.toFloat())
            allJavaTypes.fieldMixedList[6] = Mixed.valueOf(6.toDouble())
            allJavaTypes.fieldMixedList[7] = Mixed.valueOf(aString)
            allJavaTypes.fieldMixedList[8] = Mixed.valueOf(byteArray)
            allJavaTypes.fieldMixedList[9] = Mixed.valueOf(date)
            allJavaTypes.fieldMixedList[10] = Mixed.valueOf(objectId)
            allJavaTypes.fieldMixedList[11] = Mixed.valueOf(decimal128)
            allJavaTypes.fieldMixedList[12] = Mixed.valueOf(uuid)
            allJavaTypes.fieldMixedList[13] = Mixed.nullValue()
            allJavaTypes.fieldMixedList[14] = null
        }

        val allJavaTypes = realm.where<AllJavaTypes>().findFirst()

        assertEquals(true, allJavaTypes!!.fieldMixedList[0]!!.asBoolean())
        assertEquals(1, allJavaTypes.fieldMixedList[1]!!.asByte())
        assertEquals(2, allJavaTypes.fieldMixedList[2]!!.asShort())
        assertEquals(3, allJavaTypes.fieldMixedList[3]!!.asInteger())
        assertEquals(4, allJavaTypes.fieldMixedList[4]!!.asLong())
        assertEquals(5.toFloat(), allJavaTypes.fieldMixedList[5]!!.asFloat())
        assertEquals(6.toDouble(), allJavaTypes.fieldMixedList[6]!!.asDouble())
        assertEquals(aString, allJavaTypes.fieldMixedList[7]!!.asString())
        assertTrue(Arrays.equals(byteArray, allJavaTypes.fieldMixedList[8]!!.asBinary()))
        assertEquals(date, allJavaTypes.fieldMixedList[9]!!.asDate())
        assertEquals(objectId, allJavaTypes.fieldMixedList[10]!!.asObjectId())
        assertEquals(decimal128, allJavaTypes.fieldMixedList[11]!!.asDecimal128())
        assertEquals(uuid, allJavaTypes.fieldMixedList[12]!!.asUUID())
        assertTrue(allJavaTypes.fieldMixedList[13]!!.isNull)
        assertTrue(allJavaTypes.fieldMixedList[14]!!.isNull)
    }
}