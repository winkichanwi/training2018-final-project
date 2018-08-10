
# Group  ユーザー

## ユーザーエンドポイント [/api/users]

### ユーザー登録 [POST]

#### 処理概要
* ユーザー情報を新しく登録する

+ Request (application/json)
    + Attributes
        + full_name: Taro Tanaka (string, required) - 氏名
        + email: test@email.com (string, required) - メールアドレス
        + password: abcdefgh (string, required) -  パスワード (pattern: ^[0-9A-Za-z]{8,20}$)

+ Response 200

+ Response 400 (application/json)

        {
            "status_code": 4001
            "message": "Unsupported Format"
        }

##  ユーザー情報エンドポイント [/api/me]

###  ユーザー情報取得 [GET]

#### 処理概要
* ログインしてるユーザーの情報を取得する

+ Response 200 (application/json)
    + Attributes
        + id: 1 (number)
        + full_name: Taro Tanaka (string) - 氏名
        + email: test@email.com (string) - メールアドレス

###  ユーザー情報更新 [PUT]

#### 処理概要
* ログインしてるユーザーの情報を更新する

+ Request (application/json)
    + Attributes
        + id: 1 (number, required) - ユーザーID
        + full_name: Taro Tanaka (string, required) - 氏名
        + email: test@email.com (string, required) - メールアドレス

+ Response 200

+ Response 400 (application/json)

        {
            "status_code": 4001
            "message": "Unsupported Format"
        }

##  ログインエンドポイント [/api/users/login]

###  ログイン [POST]

#### 処理概要
* ユーザーのログイン情報を検証する
* 検証成功した場合、　Okステータスを返し、セッションを設定する
* 検証失敗した場合、エラーコードを返す

+ Request (application/json)
    + Attributes
        + email: test@email.com (string, required) - メールアドレス
        + password: abcdefgh (string, required) -  パスワード (pattern: ^[0-9A-Za-z]{8,20}$)

+ Response 200
    + Headers

            Set-Cookie: PLAY_SESSION={SESSION_TOKEN}&userid={USER_ID}; Path=/; HTTPOnly

+ Response 400 (application/json)

        {
            "status_code": 4001
            "message": "Unsupported Format"
        }        

+ Response 401 (application/json)

        {
            "status_code": 4011
            "message": "Authentication Failure"
        }

##  ログアウトエンドポイント [/api/users/logout]

###  ログアウト [POST]

#### 処理概要
* ユーザーのログインセッションを更新する

+ Request (application/json)

        "logout"

+ Response 200
    + Headers

            Set-Cookie: PLAY_SESSION=; Path=/; HTTPOnly

##  認証エンドポイント [/api/users/authentication]

###  認証取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する

+ Response 200

+ Response 401 (application/json)

        {
            "status_code": 4010
            "message": "Unauthorized"
        }

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

# Group   整理券

##  整理券エンドポイント [/api/tickets]

### 整理券登録 [POST]

#### 処理概要
* ユーザーのログインセッションを認証する
* 整理券を新しく登録する

+ Request (application/json)
    + Attributes
        + restaurant_id: おぼん de ごはん (string, required) - レストラン名
        + seat_no: 7 (number, required) - 人数

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

### 整理券状態更新 [PUT]

#### 処理概要
* ユーザーのログインセッションを認証する
* 整理券の状態を更新する

+ Request (application/json)
    + Attributes
        + ticket_id: 1 (number, required) - 整理券ID
        + ticket_status: Cancelled (string, required) - 整理券状態

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

##  取られた整理券情報取得エンドポイント [/api/tickets/me]

### 取られた整理券情報取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* 取られた整理券情報取得する

+ Response 200
    + Attributes (array)
        + (object)
            + ticket_id: 1 (number, required) - 整理券ID
            + restaurant_id: 1 (number, required) - 所属レストランID
            + ticket_type: A (string, required) - 整理券
            + ticket_no: 2 (number, required) - 整理券番号
            + seat_no: 2 (number, required) - 人数
            + created_at: `2018-02-01T03:15:45.000Z` (string, required) - 整理券登録日付

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

##  レストランの整理券数取得エンドポイント [/api/restaurants/{restaurantId}/tickets/counts]

### レストランの整理券数取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* 指定レストランの取られた整理券数を数えて返す

+ Parameters
    + restaurantId: 1 (number, required) - レストランID

+ Response 200
    + Attributes
        + restaurant_id: 1 (number, required) - 所属レストランID
        + ticket_counts (array)
            + (object)
                + ticket_type: A (string, required) - 整理券種類
                + count: 2 (number, required) - 整理券数
            + (object)
                + ticket_type: B (string, required) - 整理券種類
                + count: 2 (number, required) - 整理券数
            + (object)
                + ticket_type: C (string, required) - 整理券種類
                + count: 2 (number, required) - 整理券数
            + (object)
                + ticket_type: D (string, required) - 整理券種類
                + count: 2 (number, required) - 整理券数

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

##  レストランの整理券前号エンドポイント [/api/restaurants/{restaurantId}/tickets/last-called]

### レストランの整理券前号取得 [GET]

#### 処理概要
* ユーザーのログインセッションを認証する
* 指定レストランの受付された整理券の前号を返す

+ Parameters
    + restaurantId: 1 (number, required) - レストランID

+ Response 200
    + Attributes
        + restaurant_id: 1 (number, required) - 所属レストランID
        + last_called_tickets (array)
            + (object)
                + ticket_type: A (string, required) - 整理券種類
                + last_called: 2 (number, required) - 整理券前号
            + (object)
                + ticket_type: B (string, required) - 整理券種類
                + last_called: 2 (number, required) - 整理券前号
            + (object)
                + ticket_type: C (string, required) - 整理券種類
                + last_called: 2 (number, required) - 整理券前号
            + (object)
                + ticket_type: D (string, required) - 整理券種類
                + last_called: 2 (number, required) - 整理券前号

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
