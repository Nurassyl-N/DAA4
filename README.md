The main goal of this assignment was to combine several graph algorithms in one practical case related to task scheduling in a "Smart City / Smart Campus" environment.  
In such systems, some service tasks (like maintenance or cleaning) depend on others.  
If tasks form cycles, they must be detected and grouped; if not, their execution order can be optimized.

This project integrates:
- "Strongly Connected Components (SCC)" — to detect cyclic dependencies  
- "Topological Sorting" — to build a valid execution order  
- "Shortest Path and Longest Path in DAGs" — to find optimal and critical schedules  

All algorithms were implemented in "Java", and the datasets were generated in "JSON" format.


Implemented Algorithms

1. Strongly Connected Components — Kosaraju’s Algorithm

This algorithm finds all groups of nodes that are mutually reachable, meaning they form cycles.
It performs:
	1.	A DFS to record the finish times of nodes.
	2.	Reverses all edges in the graph.
	3.	Runs DFS again in decreasing order of finishing time.

This separates the graph into strongly connected components (SCCs).
Time complexity: O(V + E)


2. Topological Sort — Kahn’s Algorithm

After condensing SCCs into a DAG, topological sort was used to order the components.
Nodes with zero in-degree are added to a queue and processed iteratively.
This ensures a valid dependency order.
Time complexity: O(V + E)


3. Shortest Path in a DAG

For the acyclic part, I implemented the standard Dynamic Programming approach that relaxes edges in topological order.
This finds the minimal total duration of dependent tasks.
Time complexity: O(V + E)


4. Longest Path (Critical Path)

Using the same logic but with reversed comparison, I found the critical path,
which shows the maximum chain of dependent operations — the bottleneck of the schedule.
Time complexity: O(V + E)


Datasets
All test graphs are stored in the `/data/` folder and divided into three categories:

- **Small (6–10 vertices):** Simple cases with one or two cycles, or pure DAGs. Total 3 datasets.  
- **Medium (10–20 vertices):** Mixed structures with several SCCs. Total 3 datasets.  
- **Large (20–50 vertices):** Used for performance and timing tests. Total 3 datasets.

Each dataset includes both cyclic and acyclic examples.  
Graphs are defined in JSON with fields:  
`{ "directed": true, "n": <number>, "edges": [ {u, v, w}, ... ] }`


Analysis and Discussion

- Kosaraju’s algorithm efficiently detected all strongly connected components in near-linear time.  
- Topological sort successfully verified the DAG order of condensed components.  
- Shortest path results matched expectations, showing minimal cost between task groups.  
- Longest path correctly identified the critical chain of operations.

When comparing datasets:
- Small graphs were useful for verifying correctness.  
- Medium graphs tested performance with multiple SCCs.  
- Large graphs showed good scalability with linear complexity.


Performance Metrics

Algorithm performance summary:

- SCC (Kosaraju): 24 DFS visits, 0.119 ms, complexity O(V + E).  
- Topological Sort: 8 pushes and 8 pops, 0.066 ms, complexity O(V + E).  
- DAG Shortest Path: 7 relaxations, 0.026 ms, complexity O(V + E).  
- DAG Longest Path: 7 relaxations, 0.070 ms, complexity O(V + E).


Conclusions

This project demonstrated how multiple graph algorithms can be combined to solve real scheduling problems.  
SCC detection helped to compress cyclic dependencies, and DAG algorithms provided optimal execution plans.

In Smart City scheduling systems:
- SCC detection ensures no infinite loops in dependencies.  
- Topological sorting orders tasks correctly for execution.  
- Shortest and longest path computations identify optimal and critical routes.

This work helped me understand how graph theory and algorithmic design are used in real-world optimization systems.



## 🧠 Time Complexity Summary

- Kosaraju (SCC): O(V + E) time, O(V) space  
- Topological Sort: O(V + E) time, O(V) space  
- DAG Shortest Path: O(V + E) time, O(V) space  
- DAG Longest Path: O(V + E) time, O(V) space  
