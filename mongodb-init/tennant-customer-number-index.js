db.customers.createIndex(
    { tenantId: 1, customerNumber: 1 },
    { name: "unique_tenantId_customerNumber", unique: true }
)
