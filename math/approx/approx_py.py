import random
import math

def calc_py(count):
    count_in_circle = 1
    total = 1


    for i in range(count):
        count = 1
        x = random.random()
        y = random.random()

        euc_distance = math.sqrt((x - 0.5) ** 2 + (y - 0.5) ** 2)
        if euc_distance < 0.5:
            count_in_circle += 1
        total += 1

        approx = (count_in_circle / total) * 4

    print(approx)

calc_py(10000000)