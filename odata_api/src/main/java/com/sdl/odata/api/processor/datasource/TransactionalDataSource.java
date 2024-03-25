/**
 * Copyright (c) 2014-2024 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata.api.processor.datasource;

/**
 * Extension to traditional {@link DataSource} overloading existing API
 * methods with the addition of transaction management parameters.
 * Transactional DataSource API that will be used for batch requests.
 */
public interface TransactionalDataSource extends DataSource {


    /**
     * Commits the datasource transaction to the datasource.
     *
     * @return True if the transaction was succesfully stored in the datasource, False if not
     */
    boolean commit();

    /**
     * Rolls back the active transaction if still active.
     */
    void rollback();

    /**
     * Returns true if this transaction is still active, False if not. This can happen when the transaction was
     * already rolledback or committed.
     *
     * @return True if the transaction is still active, False if no longer active
     */
    boolean isActive();
}
