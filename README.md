# training2018-final-project

## プロジェクト作成の流れ
1. 計画 (0.5日)
2. 環境構築 (1.5日)
- Docker image 作成
  - sbt project (with Play & Slick)
  - Angular project
- AWSでの環境構築
  - IAM, VPC
  - Docker image をECS にpush
  - RDSでDatabaseの初期化
  - S3でFile systemの初期化
3. サービスの構成計画 (1日)
- サービスロジックを計画
- クラス構成を計画
- フロントのストーリーボードを設計
4. DB設計 (1日)
- ERFluteを運用してSchemaを設計する
- SQLの実装
5. Phase 1 の実装: UserのCRUD (2日)
6. Phase 2 の実装: RestaurantのCRUD (1日)
7. Phase 3 の実装: Service logic (5日)
8. Phase 4 の実装: UX UI Design (2日)
9. セキュリティー見直す (1日)
10. 最終修正位 (1日)
11. パワポー作成 (1日)
12. プレゼン練習 (1日)

## Docker images (sbt project)
**1. Create image**
`$ docker build -t restaurant-remote-reception . `

**2. Run docker container**
` $ docker run -it -p 9000:9000 --rm restaurant-remote-reception `

**3. Interact with container**
` $ curl -I localhost:9000 `
