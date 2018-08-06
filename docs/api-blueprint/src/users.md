FORMAT: 1A

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
            "status_code": 4010
            "message": "Unauthorized"
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
