# 🎨 アプリケーション ブループリント (設計図)

## 1. アプリ名（仮）

* **コメカン**: 手持ちのコスメをパレットのように管理し、新しい組み合わせ（＝レシピ）を生み出す場所。

## 2. 技術スタック

### フロントエンド (みぶきが頑張る)
* ~ **Flutter** 1つのコードベースでiOS/Androidに対応。~ できたらやる
* **言語:** Kotlin

### バックエンド (かやが頑張る)
* **言語:** Java (Spring Boot)
* **API:** REST API (JSON形式)

### クラウド (AWS)
* **実行環境:** **AWS Elastic Beanstalk**
    * 理由: Spring Bootアプリケーション（JARファイル）をデプロイするのが最も簡単。
* **データベース:** **Amazon RDS (MySQL or PostgreSQL)**
    * 理由: コスメ、ユーザー、レシピの関係性が明確なため、リレーショナルDBが適している。
* **ストレージ:** **Amazon S3**
    * 理由: ユーザーがアップロードする画像（コスメの写真、自撮り）の保存先。
* **認証:** **Amazon Cognito**
    * 理由: ユーザー認証（サインアップ、ログイン）の管理をAWSに一任できる。Flutterからの連携も容易。
* **APIゲートウェイ:** **Amazon API Gateway** (オプション)
    * 理由: Elastic Beanstalkへのトラフィック管理、APIキー設定、キャッシュなどが可能。ハッカソンでは省略し、直接Elastic Beanstalkに接続してもOK。

## 3. データベース設計 (Amazon RDS)

リレーショナルモデル（SQL）のテーブル設計案です。

1.  **`users`**
    * `user_id` (VARCHAR, Primary Key) - *Cognitoが発行するSub(UUID)を格納*
    * `username` (VARCHAR)
    * `email` (VARCHAR, Unique)
    * `profile_image_url` (TEXT)
    * `created_at` (TIMESTAMP)

2.  **`master_cosmetics`** (全ユーザー共通のコスメマスターデータ)
    * `master_cosmetic_id` (BIGINT, Primary Key, Auto-Increment)
    * `name` (VARCHAR)
    * `brand` (VARCHAR)
    * `category_large` (VARCHAR)
    * `category_small` (VARCHAR)
    * `jan_code` (VARCHAR, Index)
    * `image_url` (TEXT)

3.  **`user_cosmetics`** (ユーザーの「手持ちコスメ」リスト)
    * `user_cosmetic_id` (BIGINT, Primary Key, Auto-Increment)
    * `user_id` (FK to `users.user_id`)
    * `master_cosmetic_id` (FK to `master_cosmetics.master_cosmetic_id`)
    * `purchase_date` (DATE)
    * `memo` (TEXT)
    * `created_at` (TIMESTAMP)

4.  **`tags`** (ユーザーが作成したタグ)
    * `tag_id` (BIGINT, Primary Key, Auto-Increment)
    * `user_id` (FK to `users.user_id`)
    * `tag_name` (VARCHAR)
    * *(`user_id` と `tag_name` で複合Unique制約)*

5.  **`user_cosmetic_tags`** (手持ちコスメとタグの中間テーブル)
    * `user_cosmetic_id` (FK to `user_cosmetics.user_cosmetic_id`)
    * `tag_id` (FK to `tags.tag_id`)
    * *(Primary Key (user_cosmetic_id, tag_id))*

6.  **`makeup_recipes`** (メイクレシピ)
    * `recipe_id` (BIGINT, Primary Key, Auto-Increment)
    * `user_id` (FK to `users.user_id`)
    * `title` (VARCHAR)
    * `description` (TEXT)
    * `selfie_image_url` (TEXT) - *S3へのパス*
    * `instagram_post_url` (TEXT)
    * `created_at` (TIMESTAMP)

7.  **`recipe_items`** (レシピ使用アイテムの中間テーブル)
    * `recipe_id` (FK to `makeup_recipes.recipe_id`)
    * `user_cosmetic_id` (FK to `user_cosmetics.user_cosmetic_id`)
    * *(Primary Key (recipe_id, user_cosmetic_id))*

## 4. APIエンドポイント設計 (Spring Boot)

Flutterアプリが呼び出す主要なAPIの例です。

### Auth (Cognitoが主に処理)
* `POST /auth/register`
* `POST /auth/login`
* `POST /auth/refresh_token`

### Manage (要認証)
* `GET /cosmetics` (マイリスト取得、クエリでカテゴリ・タグ絞り込み)
* `POST /cosmetics` (手持ちコスメの登録)
* `GET /cosmetics/search` (マスターコスメ検索 `?q=...`)
* `GET /cosmetics/scan` (バーコード検索 `?jan=...`)
* `GET /statistics` (統計データ取得)

### Social (要認証)
* `GET /feed` (レシピのタイムライン取得)
* `POST /recipes` (レシピ投稿)
* `GET /recipes/{recipeId}` (レシピ詳細)
* `GET /news` (新発売コスメ情報)

## 5. 機能と画面のフロー

* 上記で設計した **REST API** を `http` や `dio` などのパッケージを使って呼び出すように実装します。
* 認証は **Cognito** と連携するFlutterパッケージ (例: `amazon_cognito_identity_dart_2`) を使用し、取得したJWTトークンをAPIリクエストのヘッダーに付与します。