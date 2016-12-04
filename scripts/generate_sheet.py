#!/usr/bin/env python
# -*- coding: utf-8 -*-

import pandas as pd

k = pd.read_csv("output/general_info/to_kcnf.csv", sep=';', header=None)
s = pd.read_csv("output/general_info/sy4ncl.csv", sep=';', header=None)
b = pd.read_csv("output/general_info/bliss.csv", sep=';', header=None)
bp = pd.read_csv("output/general_info/bliss-proc.csv", sep=';', header=None)
# generators
gen = pd.read_csv("output/general_info/bliss-proc-stats.txt", sep=",", header=None)
m = pd.read_csv("output/general_info/mappings.csv", sep=';', header=None)
al = pd.read_csv("output/general_info/all_ontologies.csv")

dl2ml_t = al[" DL2ML_TOTAL_TIME"]
k_t = k[1]
s_t = s[1]
b_t = b[1]
bp_t = bp[1]
m_t = m[1]
g = gen[12].astype(int)
h = ["To KCNF", "Sy5ncl", "Bliss", "Bliss proc", "Mappings", "Generators", "Total time"]

tt = sum(list(map(lambda x: x.values, [k_t, s_t, b_t, bp_t, m_t, dl2ml_t])))

df = pd.DataFrame({h[0]:k_t,
                   h[1]:s_t,
                   h[2]:b_t,
                   h[5]:g,
                   h[3]:bp_t,
                   h[4]:m_t,
                   h[6]:tt})
df = df[h]
df = pd.concat([al, df], axis=1)
df = df.sort_values(by=["Generators"], ascending=False)
df.to_excel("output/general_info/info.xlsx", sheet_name='GlobalSheet', index=False)
