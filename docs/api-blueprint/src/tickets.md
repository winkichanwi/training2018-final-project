FORMAT: 1A

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
