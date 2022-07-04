# atm-services
This application expose two type of services.
At the moment it doesn't support authorization for user and admin and it can
be enhanced with the support of IDP.
## Admin Mode Services
### Load Account Details in system
Request
```json
curl -X POST \
  http://localhost:8080/atm/admin/add/account \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 96308fe0-6b71-a746-9350-64d12be92d63' \
  -d '{"accountDetails" : 
	[
	{
		"accountNumber" : "123456789",
		"accountPin"    : "1234",
		"openingBal"    : 800,
		"overdraftLimit": 200
	},
	{
		"accountNumber" : "987654321",
		"accountPin"    : "4321",
		"openingBal"    : 1230,
		"overdraftLimit": 150
	}
]
}'
```

### Load Cash details in system
```
curl -X POST \
  http://localhost:8080/atm/admin/load/cash \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 65a2f3b5-efd0-456a-3d6d-639cdacd1325' \
  -d '{
	"denominationMap" : {"50" : "10", "20" : "30", "10" : "30", "5" : "20"}
}'
```
### Fetch Cash Inventory Current status
Request
```json
curl -X GET \
  http://localhost:8080/atm/report/cash/inventory \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 4516a3ab-3cb8-0333-f7e8-b20aef836a9b'
```
Response
```json
{
    "totalCash": 940,
    "denomination": {
        "5": 20,
        "10": 28,
        "20": 28,
        "50": 0
    }
}
```
### Fetch ATM Transactions
Request
```json
curl -X GET \
  http://localhost:8080/atm/report/transactions \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 803bcde3-9625-18a8-5866-18bee4242ae3'
```
Response
```json
{
    "transactions": [
        {
            "transactionId": 1,
            "transactionType": "WITHDRAWAL",
            "amount": 60,
            "denominationPair": {
                "10": 1,
                "50": 1
            }
        },
        {
            "transactionId": 2,
            "transactionType": "WITHDRAWAL",
            "amount": 240,
            "denominationPair": {
                "20": 2,
                "50": 4
            }
        },
        {
            "transactionId": 3,
            "transactionType": "WITHDRAWAL",
            "amount": 260,
            "denominationPair": {
                "10": 1,
                "50": 5
            }
        }
    ]
}
```
## User Mode Services
This service allow to login into ATM and request for cash withdrawal and balance enquiry. 
### Validate Customer Account
Request
```json
curl -X POST \
  http://localhost:8080/atm/auth/validate/accNumber \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: cae745f0-5ba4-3ddb-2d98-a31914c5e22f' \
  -d '{
	"accNumber" : "123456789"
}'
```
Response
```json
{
    "sessionId": "4a098106-6749-455b-8d08-5e7c61c33713"
}
```
### Validate Session Id along with account pin
Request
```json
curl -X POST \
  http://localhost:8080/atm/auth/login \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 5d1c1faa-1156-ed16-b44e-b27334ae18d0' \
  -d '{
	"sessionId" : "4fd0ecc4-7874-4891-a4ff-1c2dd5c131df",
	"accountPin" : "1234"
}'
```

Response
```json
{
    "authenticated": true
}
```

### Balance Inquiry Service
Request
```json
{
	"sessionId" : "bdc3cee2-1524-48a2-8991-7aa595283a9e"
}
```
Response
```json
{
    "accountBal": 800,
    "withdrawableBal": 1000
}
```
### Cash Withdrawal Service
Request
```json
{
	"sessionId" : "be53fa6f-d13e-4dca-805d-8963d9d6c054",
	"amount"    : 260
}
```
Response
```json
{
    "transactionId": "3",
    "denominationMap": {
        "10": 1,
        "50": 5
    }
}
```

# ATM Error Codes
|Error Codes | Error Messsages|
|------------|----------------|
|ATM_101     |Incorrect Account Number|
|ATM_103     |Account doesn't have enough balance|
|ATM_104     |Internal Error, please contact customer support team|
|ATM_105     |Internal Error, please contact technical support team|
|ATM_106     | Session Expired|
|ATM_107     | Invalid Session Id|
|ATM_108     | ATM doesn't have enough cash available|
ATM_109      | Withdrawal amount should be in multiples of available denomination|