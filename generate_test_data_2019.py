"""
Generate 2019-only test data SQL for Yunnan employment system.

Usage:
    python backend/generate_test_data_2019.py \
        --enterprises 50 \
        --notices 10 \
        --coverage 0.6 \
        --seed 42 \
        --output test_data_2019.sql

Key rules:
- Only 2019 months (2019-01 ~ 2019-12) as periods.
- Enterprises, accounts, reports, analysis rows all within 2019.
- Status of generated reports is archived (status=4) to support analysis.
- Uses constants from frontend for region/industry/nature/reduction types.
"""

import argparse
import calendar
import json
import random
import string
from datetime import datetime, timedelta
from pathlib import Path
from typing import Dict, List, Tuple

ROOT = Path(__file__).resolve().parents[1]

REGION_PATH = ROOT / "frontend/yunnan-enterprise-web/src/constants/yunnan_region_code.json"
INDUSTRY_PATH = ROOT / "frontend/yunnan-enterprise-web/src/constants/enterprise_industries.json"
NATURE_PATH = ROOT / "frontend/yunnan-enterprise-web/src/constants/enterprise_types.json"
REDUCTION_TYPES_PATH = ROOT / "frontend/yunnan-enterprise-web/src/constants/employment_reduction_types.json"


def load_json(path: Path) -> List[dict]:
    with path.open("r", encoding="utf-8") as f:
        return json.load(f)


def top_level(items: List[dict]) -> List[dict]:
    return [i for i in items if i.get("parentId") == 0]


def random_datetime_in_month(year: int, month: int) -> datetime:
    _, last_day = calendar.monthrange(year, month)
    day = random.randint(1, last_day)
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    second = random.randint(0, 59)
    return datetime(year, month, day, hour, minute, second)


def escape(val: str) -> str:
    return val.replace("'", "''")


def fmt_dt(dt: datetime) -> str:
    return dt.strftime("%Y-%m-%d %H:%M:%S")


def make_periods() -> List[Dict]:
    periods = []
    period_id = 1
    for month in range(1, 13):
        start_dt = datetime(2019, month, 1, 0, 0, 0)
        end_day = calendar.monthrange(2019, month)[1]
        end_dt = datetime(2019, month, min(25, end_day), 23, 59, 59)
        periods.append(
            {
                "period_id": period_id,
                "investigate_time": f"2019-{month:02d}",
                "period_start_time": fmt_dt(start_dt),
                "period_end_time": fmt_dt(end_dt),
                "enterprise_count": 0,
                "created_at": fmt_dt(start_dt),
                "updated_at": fmt_dt(start_dt),
            }
        )
        period_id += 1
    return periods


def pick_codes(options: List[dict]) -> Tuple[int, int]:
    """Return (top_code, deepest_code) from hierarchical constant list."""
    top = random.choice(options)
    top_code = int(top["code"])

    # Walk down one level if present (industry/nature) and two levels for regions
    current = top
    if current.get("children"):
        current = random.choice(current["children"])
    if current.get("children"):
        current = random.choice(current["children"])

    return top_code, int(current["code"])


def generate_enterprises(
    count: int,
    regions: List[dict],
    industries: List[dict],
    natures: List[dict],
) -> List[Dict]:
    enterprises = []
    for idx in range(1, count + 1):
        enterprise_id = f"ENT{idx:05d}"
        org_code = "".join(random.choices(string.digits, k=10))
        name = f"Enterprise {idx:05d}"
        contact = f"Contact{idx:03d}"
        region_code, region_full = pick_codes(regions)
        nature_code, nature_full = pick_codes(natures)
        industry_code, industry_full = pick_codes(industries)
        created_at = random_datetime_in_month(2019, random.randint(1, 12))
        enterprises.append(
            {
                "enterprise_id": enterprise_id,
                "org_code": org_code,
                "name": name,
                "region": region_full,
                "region_code": region_code,
                "nature": nature_full,
                "nature_code": nature_code,
                "industry": industry_full,
                "industry_code": industry_code,
                "industry_desc": f"Primary business for {name}",
                "contact_name": contact,
                "address": f"Address {idx}",
                "postal_code": f"{random.randint(650000, 659999)}",
                "phone_num": f"138{random.randint(10000000, 99999999)}",
                "fax_num": f"0871{random.randint(1000000, 9999999)}",
                "email": f"{enterprise_id.lower()}@example.com",
                "status": 3,  # 正常
                "created_at": fmt_dt(created_at),
                "updated_at": fmt_dt(created_at),
            }
        )
    return enterprises


def generate_accounts(enterprises: List[Dict], regions: List[dict]) -> List[Dict]:
    accounts = []
    # City accounts (one per top-level region)
    for r in top_level(regions):
        accounts.append(
            {
                "username": f"city_{int(r['code']):02d}",
                "password": "123456",
                "type": 2,
                "enterprise_id": None,
                "city_code": int(r["code"]),
                "created_at": fmt_dt(datetime(2019, 1, 1, 9, 0, 0)),
                "status": 0,
            }
        )
    # Enterprise accounts
    for ent in enterprises:
        accounts.append(
            {
                "username": f"user_{ent['enterprise_id'].lower()}",
                "password": "123456",
                "type": 1,
                "enterprise_id": ent["enterprise_id"],
                "city_code": None,
                "created_at": ent["created_at"],
                "status": 0,
            }
        )
    return accounts


def generate_notices(count: int) -> List[Dict]:
    notices = []
    for idx in range(1, count + 1):
        start_dt = random_datetime_in_month(2019, random.randint(1, 12))
        end_dt = start_dt + timedelta(days=random.randint(5, 30))
        notices.append(
            {
                "title": f"Notice {idx:03d}",
                "content": f"Content for notice {idx:03d}",
                "attachment": None,
                "attachment_name": None,
                "is_important": random.choice([0, 1]),
                "notice_status": random.choice([1, 2, 3, 4]),
                "publisher": "admin",
                "publish_time": fmt_dt(start_dt),
                "start_time": fmt_dt(start_dt),
                "end_time": fmt_dt(end_dt),
                "read_count": random.randint(0, 30),
                "status": 1,
                "created_at": fmt_dt(start_dt),
                "updated_at": fmt_dt(start_dt),
            }
        )
    return notices


def generate_reports_and_analysis(
    enterprises: List[Dict],
    periods: List[Dict],
    coverage: float,
    reduction_types: List[dict],
) -> Tuple[List[Dict], List[Dict], List[Dict], List[Dict]]:
    reports = []
    enterprise_reports = []
    audits = []
    analysis_rows = []
    reduction_map = {idx + 1: r for idx, r in enumerate(reduction_types)}

    report_seq = 1
    for ent in enterprises:
        for period in periods:
            if random.random() > coverage:
                continue
            rpt_id = f"RPT{report_seq:08d}"
            report_seq += 1

            construction = random.randint(80, 500)
            # allow slight change up/down
            investigation = max(10, construction + random.randint(-50, 80))
            reduction_type_id = random.randint(1, min(5, len(reduction_map)))
            reason1 = reduction_type_id
            reason2 = random.randint(1, min(5, len(reduction_map)))
            reason3 = random.randint(1, min(5, len(reduction_map)))

            reports.append(
                {
                    "report_id": rpt_id,
                    "construction_count": construction,
                    "investigation_count": investigation,
                    "reduction_type": reduction_type_id,
                    "reason1": reason1,
                    "reason2": reason2,
                    "reason3": reason3,
                    "reason1_desc": reduction_map[reason1]["label"],
                    "reason2_desc": reduction_map[reason2]["label"],
                    "reason3_desc": reduction_map[reason3]["label"],
                    "other_reason": None,
                }
            )

            created_at = random_datetime_in_month(2019, int(period["investigate_time"].split("-")[1]))
            enterprise_reports.append(
                {
                    "enterprise_id": ent["enterprise_id"],
                    "period_id": period["period_id"],
                    "report_id": rpt_id,
                    "old_report_id": None,
                    "reason_return": None,
                    "status": 4,
                    "period_start_time": period["period_start_time"],
                    "period_end_time": period["period_end_time"],
                    "created_at": fmt_dt(created_at),
                    "updated_at": fmt_dt(created_at),
                    "enterprise_nature": ent["nature_code"],
                    "enterprise_industry": ent["industry_code"],
                    "enterprise_region": ent["region_code"],
                }
            )

            # audit history: city approve then province approve
            audits.append(
                {
                    "enterprise_id": ent["enterprise_id"],
                    "period_id": period["period_id"],
                    "report_id": rpt_id,
                    "audit_level": 1,
                    "auditor": f"city_{ent['region_code']:02d}",
                    "audit_result": 1,
                    "audit_opinion": "City approved",
                    "audit_time": fmt_dt(created_at + timedelta(hours=2)),
                }
            )
            audits.append(
                {
                    "enterprise_id": ent["enterprise_id"],
                    "period_id": period["period_id"],
                    "report_id": rpt_id,
                    "audit_level": 2,
                    "auditor": "admin",
                    "audit_result": 1,
                    "audit_opinion": "Province approved",
                    "audit_time": fmt_dt(created_at + timedelta(hours=6)),
                }
            )
            change = investigation - construction
            change_ratio = round((change / construction) * 100, 2)
            unemployed = max(0, construction - investigation)
            analysis_rows.append(
                {
                    "enterprise_id": ent["enterprise_id"],
                    "period_id": period["period_id"],
                    "report_id": rpt_id,
                    "region": ent["region"],
                    "region_code": ent["region_code"],
                    "industry": ent["industry"],
                    "industry_code": ent["industry_code"],
                    "nature": ent["nature"],
                    "nature_code": ent["nature_code"],
                    "employed_count": investigation,
                    "construction_employed": construction,
                    "unemployed_count": unemployed,
                    "unemployment_rate": change_ratio,
                    "created_time": fmt_dt(created_at),
                    "updated_time": fmt_dt(created_at),
                }
            )
    return reports, enterprise_reports, audits, analysis_rows


def build_insert(table: str, rows: List[Dict]) -> str:
    if not rows:
        return ""
    cols = list(rows[0].keys())
    values_sql = []
    for row in rows:
        vals = []
        for col in cols:
            v = row[col]
            if v is None:
                vals.append("NULL")
            elif isinstance(v, (int, float)):
                vals.append(str(v))
            else:
                vals.append(f"'{escape(str(v))}'")
        values_sql.append("(" + ", ".join(vals) + ")")
    return f"INSERT INTO {table} ({', '.join(cols)}) VALUES\n  " + ",\n  ".join(values_sql) + ";\n\n"


def main():
    parser = argparse.ArgumentParser(description="Generate 2019-only SQL test data")
    parser.add_argument("--enterprises", type=int, default=None, help="Number of enterprises to generate")
    parser.add_argument("--notices", type=int, default=None, help="Number of notices to generate")
    parser.add_argument(
        "--coverage",
        type=float,
        default=None,
        help="Probability that an enterprise has a report in a given month (0~1)",
    )
    parser.add_argument("--seed", type=int, default=None, help="Random seed for reproducibility")
    parser.add_argument("--output", type=Path, default=None, help="Output SQL file path")
    args = parser.parse_args()

    def prompt_int(prompt: str, default: int, minimum: int = 0, maximum: int = None) -> int:
        while True:
            raw = input(f"{prompt} [{default}]: ").strip()
            if raw == "":
                return default
            try:
                val = int(raw)
                if val < minimum:
                    print(f"Value must be >= {minimum}")
                    continue
                if maximum is not None and val > maximum:
                    print(f"Value must be <= {maximum}")
                    continue
                return val
            except ValueError:
                print("Please enter an integer.")

    def prompt_float(prompt: str, default: float, minimum: float = 0.0, maximum: float = 1.0) -> float:
        while True:
            raw = input(f"{prompt} [{default}]: ").strip()
            if raw == "":
                return default
            try:
                val = float(raw)
                if val < minimum or val > maximum:
                    print(f"Value must be between {minimum} and {maximum}")
                    continue
                return val
            except ValueError:
                print("Please enter a number.")

    def prompt_str(prompt: str, default: str) -> str:
        raw = input(f"{prompt} [{default}]: ").strip()
        return raw or default

    enterprises_count = args.enterprises if args.enterprises is not None else prompt_int("Enterprises count", 20, 1)
    notices_count = args.notices if args.notices is not None else prompt_int("Notices count", 10, 0)
    coverage_val = args.coverage if args.coverage is not None else prompt_float("Report coverage (0~1)", 0.5, 0.0, 1.0)
    seed_val = args.seed if args.seed is not None else prompt_int("Random seed", 42, 0)
    output_path = args.output if args.output is not None else Path(prompt_str("Output file", "test_data_2019.sql"))

    random.seed(seed_val)

    regions = load_json(REGION_PATH)
    industries = load_json(INDUSTRY_PATH)
    natures = load_json(NATURE_PATH)
    reduction_types = load_json(REDUCTION_TYPES_PATH)

    periods = make_periods()
    enterprises = generate_enterprises(enterprises_count, top_level(regions), top_level(industries), top_level(natures))
    accounts = generate_accounts(enterprises, regions)
    notices = generate_notices(notices_count)
    reports, enterprise_reports, audits, analysis_rows = generate_reports_and_analysis(
        enterprises, periods, coverage_val, reduction_types
    )

    sql_parts = []
    sql_parts.append("-- Auto-generated 2019 test data\n\nSET NAMES utf8mb4;\nSET FOREIGN_KEY_CHECKS=0;\n\n")
    sql_parts.append(build_insert("period_info", periods))
    sql_parts.append(build_insert("enterprise_info", enterprises))
    sql_parts.append(build_insert("account_info", accounts))
    sql_parts.append(build_insert("notice_info", notices))
    sql_parts.append(build_insert("report_info", reports))
    sql_parts.append(build_insert("enterprise_report_info", enterprise_reports))
    sql_parts.append(build_insert("report_audit_history", audits))
    sql_parts.append(build_insert("enterprise_analysis_data", analysis_rows))
    sql_parts.append("SET FOREIGN_KEY_CHECKS=1;\n")

    output_path.write_text("".join(sql_parts), encoding="utf-8")
    print(f"Generated SQL with {len(periods)} periods, {len(enterprises)} enterprises, "
          f"{len(reports)} reports, {len(notices)} notices -> {output_path}")


if __name__ == "__main__":
    main()
