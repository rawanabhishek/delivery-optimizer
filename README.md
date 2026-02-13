Here is a concise `README.md` focusing specifically on the algorithmic approach and logic.

---

# Route Optimization Logic

## Core Problem

The objective is to find the sequence of stops that results in the **shortest possible time** to complete a batch of  orders. This is a variation of the **Traveling Salesperson Problem (TSP)** with two specific constraints:

1.
**Precedence:** A Restaurant () must be visited before its corresponding Consumer ().


2. **Time Windows:** Restaurants have a meal-preparation time (). If the executive arrives early, they must wait.



## Algorithmic Approach: Recursive Backtracking (Branch & Bound)

Since delivery batches are typically small (e.g., 2-5 orders), we use a **Depth-First Search (DFS)** strategy to explore valid permutations of stops.

### 1. Graph Representation

We model the delivery zone as a graph where nodes represent:

*
**Start:** The executive's initial location.


*
**Pickup Nodes:** Restaurants ().


*
**Delivery Nodes:** Consumers ().



### 2. Execution Logic

The algorithm recursively builds a path step-by-step:

1. **Start** at the executive's current location.
2. **Iterate** through all unvisited nodes to find the next valid stop.
3. **Validation (Pruning):**
* *Is it a Restaurant?*  Valid (if unvisited).
* *Is it a Consumer?*  Valid **only if** the specific Restaurant for this order has already been visited.


4. **Cost Calculation:**
*
**Travel Time:** Calculated using the **Haversine formula** with an average speed of **20 km/hr**.


* **Wait Time:** If `Arrival Time < Prep Time`, the executive waits.
* .




5. **Optimization:**
* Track the `minTime` found so far.
* If a partial path's time exceeds `minTime`, abandon that branch (Bounding) to save computation.



### 3. Complexity

* **Time Complexity:**  worst case.
* **Feasibility:** Highly efficient for , making it suitable for real-time delivery batching.

## Physics & Formulas

*
**Distance:** Great-circle distance between coordinates (Haversine).


*
**Speed:** Constant .


* **Total Duration:** .