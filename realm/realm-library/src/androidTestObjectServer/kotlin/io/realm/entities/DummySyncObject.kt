/**
 * Copyright 2020 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.realm.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmField
import org.bson.types.ObjectId
import java.util.*

open class DummySyncObject: RealmObject() {
    @PrimaryKey
    var _id: ObjectId? = ObjectId.get()
    var string: String = UUID.randomUUID().toString()
}

open class DummySyncObjectWithPartition: RealmObject() {
    @PrimaryKey
    var _id: ObjectId? = ObjectId.get()
    var string: String = UUID.randomUUID().toString()

    @RealmField(name = "realm_id")
    var realmId: String? = null
}
