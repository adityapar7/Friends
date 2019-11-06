package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;
import java.io.*;

public class Tester {
	
	
	//removes pairs when theres an island of two	
	private static ArrayList<String> modify(ArrayList<String> answer, Graph g) {
		for (int i=0; i<=answer.size()-2; i++) {
			for (int j=i+1; j<=answer.size()-1;j++) {
				String first=answer.get(i);
				String second=answer.get(j);
				
				Person personFirst=g.members[g.map.get(first)];
				Person personSecond=g.members[g.map.get(second)];
				
				//if the personFirst's friend's name is the same as second
				if ((g.members[personFirst.first.fnum].name.equals(second)&& personFirst.first.next==null)&&
						(g.members[personSecond.first.fnum].name.equals(first)&& personSecond.first.next==null)) {
					if (answer.size()==2) {
						return null;
					}
					answer.remove(i);
					answer.remove(j+1);
					i=-1;
					break;
				}	
			}
		}
		return answer;
	}
	
    //find size of dfsnum because counting up and passing it back in recursion does not work
    //to assign the dfsnum and back number
    private static int sizeArr(int[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) count++;
        }
        return count;
    }
	
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		/** COMPLETE THIS METHOD **/
		if (g.map.get(p1)==null||g.map.get(p2)==null) return null;
		ArrayList<String> answer= new ArrayList<String>();
		Queue<Person> next= new Queue<Person>();
		
		//if p1 equals p2
		if (p1.equals(p2)) {
			answer.add(p1);
			return answer;
		}
		
		//array that says if a vertex was visited
		//all values are set to false
		boolean[] visits= new boolean[g.members.length];
		for (int i=0; i<visits.length;i++) {
			visits[i]=false;
		}
		
		//FINDS REFERENCE TO p1 LINKED LIST OF Friends
		//start is Person with name p1
		int indexp1= g.map.get(p1);
		//answer.add(p1);
		//set visit array for start index to "visited"
		visits[indexp1]=true;
		Person current= g.members[indexp1];
		next.enqueue(current);
		
		//BFS
		while (next.isEmpty()==false) {
			current=next.dequeue();
			//vertex is p1's first Friend
			Friend vertex= current.first;
			//loads unvisited friends of a vertex into the queue
			while (vertex!=null) {
				if (visits[vertex.fnum]==false) {
					next.enqueue(g.members[vertex.fnum]);
					visits[vertex.fnum]=true;
					
					//if target is found
					if (g.members[vertex.fnum].name.equals(p2)) {
						answer.add(g.members[vertex.fnum].name);
						answer.addAll(0,shortestChain(g, p1,current.name));
						return answer;
					}
				}
				vertex=vertex.next;
			}
		}
		return null;
	}
	
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		/** COMPLETE THIS METHOD **/
		ArrayList<ArrayList<String>> answer= new ArrayList<ArrayList<String>>();
		boolean[] visits= new boolean[g.members.length];
		for (int i=0;i<visits.length;i++) {
			visits[i]=false;
		}
		
		for (int i=0; i<g.members.length;i++) {
			//checks if it is a student and if they have not been visited yet
			if(g.members[i].student && visits[i]==false) {
				//checks if the school of the person is the same as the target school
				if (g.members[i].school.equals(school)) {
					
					//creates new ArrayList to add to the answer
					ArrayList<String> addThis= new ArrayList<String>();
					addThis.add(g.members[i].name);
					
					//adds all the people in the clique of the first match
					int j=0;
					while (visits[g.map.get(addThis.get(addThis.size()-1))]==false && j<=addThis.size()-1) {
						if (visits[g.map.get(addThis.get(j))]==false) {
							//sets Person to visited
							int index=g.map.get(addThis.get(j));
							visits[index]=true;
							//adds Friends of the current person to addThis if they have the same school
							Friend ptr=g.members[g.map.get(addThis.get(j))].first;
							while (ptr!=null) {
								//checks if friend is student, then if they go to the same school, then if they had
								//already been visited
								if (g.members[ptr.fnum].student && g.members[ptr.fnum].school.equals(school)&&
										visits[ptr.fnum]==false && addThis.contains(g.members[ptr.fnum].name)==false) 
									addThis.add(g.members[ptr.fnum].name);
								ptr=ptr.next;
							}		
						}
						j++;
					}
					//adds clique to the answer
					if (addThis.size()!=0) answer.add(addThis);	
				}
			}
		}
		if (answer.size()==0) return null;
		return answer;
	}
	
	public static ArrayList<String> connectors(Graph g) {
		
		/** COMPLETE THIS METHOD **/
		ArrayList<String> answer= new ArrayList<String>();
		Stack<Person> stack = new Stack<Person>();
		boolean[] visits= new boolean[g.members.length];
		//all vertices start off not visited
		for (int i=0; i<= visits.length-1; i++) {
			visits[i]=false;
		}
		//array of dfs numbers for each vertex
		int[] dfsnum= new int[g.members.length];
		//array of back numbers
		int[] back= new int[g.members.length];
		
		for (int i=0; i<=g.members.length-1; i++) {
			//puts g.members[0] on the stack
			if (visits[i]==false) {
				visits[i]=true;
				System.out.print(g.members[i].name);
				stack.push(g.members[i]);
				
				//integer to add to the arrays
				int add=1;
				
				//defines where DFS begins
				Person start= g.members[0];
				
				//DFS
				while (stack.isEmpty()==false) {
					//defines current vertex
					Person v=stack.pop();
					//points to a neighbor
					Friend w= v.first;
					
					//checks if it is a connector
					/*if (dfsnum[g.map.get(v.name)]<=back[w.fnum]&& listContains(answer,v.name)==false) {
						answer.add(v.name);
					}*/
					
					//keeps adding to stack through dfs given a starting vertex
					while(w!=null) {
						if (visits[w.fnum]) {
							//If a neighbor, w, is already visited then back(v) is set to min(back(v),dfsnum(w))
							//change back[]
							back[g.map.get(v.name)]=Math.min(back[g.map.get(v.name)], dfsnum[w.fnum]);
							
						}
						if (visits[w.fnum]==false) {
							System.out.print(g.members[w.fnum].name);
							visits[w.fnum]=true;
							stack.push(g.members[w.fnum]);
							
							//changes arrays dfsnum[] and back[]
							dfsnum[w.fnum]=add;
							back[w.fnum]=add;
							add++;
							
							w=g.members[w.fnum].first;
						}
						else {
							//if it revisits the vertex
							if (w.fnum==g.map.get(start.name)) {
								//do something
							}
							//changes the arrays accordingly
							else {
								back[g.map.get(v.name)]=Math.min(back[g.map.get(v.name)], dfsnum[w.fnum]);
							}
							w=w.next;
						}
					}
				}
			}	
		}
		
		// FOLLOWING LINE IS A PLACEHOLDER TO MAKE COMPILER HAPPY
		// CHANGE AS REQUIRED FOR YOUR IMPLEMENTATION
		return answer;
		
	}
	
	public static ArrayList<String> connectors2(Graph g) {
		//if there are two people in the map and they are connected
        boolean[] visits = new boolean[g.members.length]; 
        for (int i=0;i<=g.members.length-1;i++) {
        	visits[i]=false;
        }
        int[] dfsnum = new int[g.members.length];
        int[] back = new int[g.members.length];
        ArrayList<String> answer = new ArrayList<String>();

        //driver portion
        for (Person person : g.members) {
            //if it hasn't been visited
            if (visits[g.map.get(person.name)]==false){
                //for different islands
                dfsnum = new int[g.members.length];
                dfs(g.map.get(person.name), g.map.get(person.name), g, visits, dfsnum, back, answer);
            }
        }
        
        //check if vertex has one neighbor
        for (int i = 0; i < answer.size(); i++) {
            Friend ptr = g.members[g.map.get(answer.get(i))].first;
            //counts number of neighbors
            int count = 0;
            while (ptr != null) {
                ptr = ptr.next;
                count++;
            }
            if (count == 1) answer.remove(i);
            if (count == 0) answer.remove(i);
        } 
        for (Person member : g.members) {
            if (member.first!=null&& member.first.next == null) {
               if (answer.contains(g.members[member.first.fnum].name)==false)
            		   answer.add(g.members[member.first.fnum].name);
            }
        }
        answer=modify(answer,g);
        if (answer==null||answer.size()==0) return null;
        return answer;
    }

 	//performs DFS
    private static void dfs(int v, int start, Graph g, boolean[] visits, int[] dfsnum, int[] back, ArrayList<String> answer){
        //marks person at v as visited
        Person person = g.members[v];
        visits[g.map.get(person.name)] = true;
        int count = sizeArr(dfsnum)+1;

        //sets dfsnum[] and back[]
        if (dfsnum[v] == 0 && back[v] == 0) {
            dfsnum[v] = count;
            back[v] = dfsnum[v];
        }

        //loops through neighbors of person
        for (Friend ptr = person.first; ptr != null; ptr = ptr.next) {
            //if not visited
            if (visits[ptr.fnum]==false) {
                dfs(ptr.fnum, start, g, visits, dfsnum, back, answer);

                //changes back[v] after back up
                if (back[ptr.fnum] < dfsnum[v]) back[v] = Math.min(back[ptr.fnum], back[v]);
                    
                    
                else {
                    //if starting point has two edges but is not a connector
                    if (Math.abs(dfsnum[v]-back[ptr.fnum]) < 1 && Math.abs(dfsnum[v]-dfsnum[ptr.fnum]) <=1 && back[ptr.fnum] ==1 && v == start) {
                        //don't add if both 1's
                        continue;
                    }

                    //if it is a connector
                    if (dfsnum[v] <= back[ptr.fnum] && (v != start || back[ptr.fnum] == 1 )) {
                    	//add connector to list if not in list already
                    	if (answer.contains(g.members[v].name)==false) answer.add(g.members[v].name);
                    }
                }
            }
            //update back[v] if already visited 
            else back[v] = Math.min(back[v], dfsnum[ptr.fnum]);             
        }
    }

	public static void main(String[] args) {
		File file= new File("doc6.txt");
		try {
			Scanner sc= new Scanner(file);
			Graph graph= new Graph(sc);
			System.out.println(graph.map.keySet());

			ArrayList<String> x=connectors2(graph);
			System.out.println(x);
			//ArrayList<ArrayList<String>> answer= cliques(graph,"rutgers");
			//System.out.println(answer);
			//ArrayList<String> answer= shortestChain(graph, "p301", "p198");
			//System.out.println(answer);
		} catch (FileNotFoundException e) {
			System.out.println("File not Found");
		}
	}
}
