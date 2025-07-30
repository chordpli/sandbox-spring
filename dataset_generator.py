import csv
import random
from datetime import datetime, timedelta
import hashlib

def generate_transaction_data(num_rows, start_time, account_prefix="ACC", amount_range=(100, 10000)):
    data = []
    for i in range(num_rows):
        transaction_time = start_time + timedelta(minutes=i)
        account_number = f"{account_prefix}{random.randint(10000000, 99999999)}"
        transaction_type = random.choice(['DEPOSIT', 'WITHDRAWAL'])
        amount = round(random.uniform(amount_range[0], amount_range[1]), 2)
        balance = round(random.uniform(1000, 100000), 2)
        counterparty_name = f"Party{random.randint(1, 100)}"
        memo = f"Memo {random.randint(1, 200)}"
        data.append([
            transaction_time.strftime("%Y-%m-%d %H:%M:%S"),
            account_number,
            transaction_type,
            f"{amount:.2f}",
            f"{balance:.2f}",
            counterparty_name,
            memo
        ])
    return data

def generate_csv(filename, data):
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(["transaction_time", "account_number", "transaction_type", "amount", "balance", "counterparty_name", "memo"])
        writer.writerows(data)

# Parameters
NUM_ROWS_A = 100000
NUM_ROWS_B = 100000
OVERLAP_B = 90000
NEW_B = 10000

start_time_a = datetime(2025, 1, 1, 9, 0, 0)

# Generate Dataset A
dataset_a_data = generate_transaction_data(NUM_ROWS_A, start_time_a)
generate_csv("src/test/resources/idempotent/dataset_a.csv", dataset_a_data)
print(f"Generated src/test/resources/idempotent/dataset_a.csv with {len(dataset_a_data)} rows.")

# Generate Dataset B
# Take OVERLAP_B rows from Dataset A
dataset_b_overlap = random.sample(dataset_a_data, OVERLAP_B)

# Generate NEW_B unique rows for Dataset B
start_time_b_new = start_time_a + timedelta(days=365) # Ensure new data has different timestamps
dataset_b_new = generate_transaction_data(NEW_B, start_time_b_new, account_prefix="NEWACC")

# Combine and shuffle for Dataset B
dataset_b_data = dataset_b_overlap + dataset_b_new
random.shuffle(dataset_b_data)
generate_csv("src/test/resources/idempotent/dataset_b.csv", dataset_b_data)
print(f"Generated src/test/resources/idempotent/dataset_b.csv with {len(dataset_b_data)} rows (overlap: {OVERLAP_B}, new: {NEW_B}).")