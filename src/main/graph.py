import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker

df = pd.read_csv('output/swarm_20201012-221839.csv', sep=', ', engine='python')
df_best = pd.read_csv('output/best_20201012-221839.csv', sep=', ', engine='python', header=None)

px = df['p0']
py = df['p1']
vx = df['v0']
vy = df['v1']

pbx = df_best.iloc[:, 0]
pby = df_best.iloc[:, 1]

fig, ax = plt.subplots()

loc_major = plticker.MultipleLocator(base=128.0)
loc_minor = plticker.MultipleLocator(base=32.0)
ax.xaxis.set_major_locator(loc_major)
ax.xaxis.set_minor_locator(loc_minor)
ax.yaxis.set_major_locator(loc_major)
ax.yaxis.set_minor_locator(loc_minor)
ax.set_axisbelow(True)


plt.grid(b='true', which='both')

plt.xlim(-512, 512)
plt.ylim(-512, 512)

Q = ax.quiver(px, py, vx, vy, pivot='mid')
Sb = ax.scatter(pbx, pby, color='b', s=10)
S = ax.scatter(px, py, color='r', s=2)
ax.set_xlabel('x')
ax.set_ylabel('y')
plt.suptitle('Plot Of Final Particles')
plt.title('position=red dot, velocity=arrow, best found position = blue dot')

plt.show()
