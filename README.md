# 臺灣政府開放資料

此專案透過以下技術，從臺灣政府開放資料程式抓取並處理成後續應用成果。

## 使用技術語言

- Java 11
- Maven
- Github Actions

## 目前使用到的資料如下：

### 健康保險

| 資料集 | 原始資料 | 處理成果 |
| -------- | -------- | -------- |
| [健保特約機構防疫家用快篩剩餘數量明細](https://data.nhi.gov.tw/Datasets/DatasetDetail.aspx?id=698)  | [CSV](./src/test/resources/raw/A21030000I-D03001-001.csv) | [JSON](./src/test/resources/json/nhi/rapidTestStock.json) |

## 系統排程

`每小時 55 分`觸發 `Github Actions` 進行資料蒐集及產出。
