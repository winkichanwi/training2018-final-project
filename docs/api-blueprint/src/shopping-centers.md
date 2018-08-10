FORMAT: 1A

# Group  ショッピングセンター

## ショッピングセンターエンドポイント [/api/shopping-centers]

###  ショッピングセンターリスト取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* ショッピングセンターをリストアップ

+ Response 200
    + Attributes (array)
        + (object)
            + id: 1 (number)
            + name: ルミネ (string) - ショッピングセンター名
            + branch: 新宿 (string) - ショッピングセンター支店名

+ Response 401 (application/json)

        {
            "status_code": 4010
            "message": "Unauthorized"
        }

+ Response 404 (application/json)

        {
            "status_code": 4041
            "message": "Resource Not Found"
        }

## ショッピングセンター情報取得エンドポイント [/api/shopping-centers/{shoppingCenterId}]

###  ショッピングセンター情報取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* ショッピングセンターの情報を取得する

+ Parameters
    + shoppingCenterId: 1 (number, required) - ショッピングセンターID

+ Response 200
    + Attributes
        + id: 1 (number)
        + name: ルミネ (string) - ショッピングセンター名
        + branch: 新宿 (string) - ショッピングセンター支店名

+ Response 401 (application/json)

        {
            "status_code": 4010
            "message": "Unauthorized"
        }

+ Response 404 (application/json)

        {
            "status_code": 4041
            "message": "Resource Not Found"
        }
