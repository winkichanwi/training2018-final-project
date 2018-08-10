FORMAT: 1A

# Group  レストラン

## レストランエンドポイント [/api/restaurants]

### レストラン登録 [POST]

#### 処理概要
* レストラン情報を新しく登録する

+ Request (application/json)
    + Attributes
        + name: おぼん de ごはん (string, required) - レストラン名
        + email: test@email.com (string, required) - メールアドレス
        + password: abcdefgh (string, required) - パスワード (pattern: ^[0-9A-Za-z]{8,20}$)
        + shopping_center_id: 1 (number, required) - 所属ショッピングセンターID
        + floor: 8F (string, required) - フロア
        + seat_no: 57 (number, required) - 席数
        + cuisine: 定食&カフェ (string, required) - 料理
        + phone_no: `03-6915-2611` (string, required) - 電話番号
        + opening_hour: `11:00-22:30` (string, required) - 営業時間
        + image_url: restaurant1.jpg (string, optional) - 画像URL

+ Response 200

+ Response 400 (application/json)

        {
            "status_code": 4001
            "message": "Unsupported Format"
        }

+ Response 401 (application/json)

        {
            "status_code": 4010
            "message": "Unauthorized"
        }

### レストラン情報更新 [PUT]

#### 処理概要
* レストランのログインセッションを認証する
* レストラン情報更新する

+ Request (application/json)
    + Attributes
        + id: 1 (number, required) - レストランID
        + name: おぼん de ごはん (string, required) - レストラン名
        + email: test@email.com (string, required) - メールアドレス
        + shopping_center_id: 1 (number, required) - 所属ショッピングセンターID
        + floor: 8F (string, required) - フロア
        + seat_no: 57 (number, required) - 席数
        + cuisine: 定食&カフェ (string, required) - 料理
        + phone_no: `03-6915-2611` (string, required) - 電話番号
        + opening_hour: `11:00-22:30` (string, required) - 営業時間
        + image_url: restaurant1.jpg (string, optional) - 画像URL

+ Response 200

+ Response 400 (application/json)

        {
            "status_code": 4001
            "message": "Unsupported Format"
        }

+ Response 401 (application/json)

        {
            "status_code": 4010
            "message": "Unauthorized"
        }

## レストラン情報取得エンドポイント [/api/restaurants/{restaurantId}]

### レストラン情報取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* レストラン情報取得する

+ Parameters
    + restaurantId: 1 (number, required) - レストランID

+ Response 200
    + Attributes
        + id: 1 (number, required) - レストランID
        + name: おぼん de ごはん (string, required) - レストラン名
        + email: test@email.com (string, required) - メールアドレス
        + shopping_center_id: 1 (number, required) - 所属ショッピングセンターID
        + status: Closed (string, required) - レストラン営業状態
        + floor: 8F (string, required) - フロア
        + seat_no: 57 (number, required) - 席数
        + cuisine: 定食&カフェ (string, required) - 料理
        + phone_no: `03-6915-2611` (string, required) - 電話番号
        + opening_hour: `11:00-22:30` (string, required) - 営業時間
        + image_url: restaurant1.jpg (string, optional) - 画像URL

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

## ショッピングセンターのレストランリスト取得エンドポイント [/api/shopping-centers/{shoppingCenterId}/restaurants]

### レストランリスト取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* 指定ショッピングセンターのレストランをリストアップ

+ Parameters
    + shoppingCenterId: 1 (number, required) - ショッピングセンターID

+ Response 200
    + Attributes (array)
        + (object)
            + id: 1 (number, required) - レストランID
            + name: おぼん de ごはん (string, required) - レストラン名
            + email: test@email.com (string, required) - メールアドレス
            + shopping_center_id: 1 (number, required) - 所属ショッピングセンターID
            + status: Closed (string, required) - レストラン営業状態
            + floor: 8F (string, required) - フロア
            + seat_no: 57 (number, required) - 席数
            + cuisine: 定食&カフェ (string, required) - 料理
            + phone_no: `03-6915-2611` (string, required) - 電話番号
            + opening_hour: `11:00-22:30` (string, required) - 営業時間
            + image_url: restaurant1.jpg (string, optional) - 画像URL
