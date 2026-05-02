# AVL树

## 1. 概念

AVL 树是最早提出的自平衡二叉查找树。  
它要求任意节点左右子树高度差绝对值不超过 1。

```
AVL 树示例（标注平衡因子）:

            [30] (0)           ← 平衡因子 = 左高 - 右高
           /      \
        [20] (0)  [40] (-1)
        /   \         \
     [10]  [25]      [50] (0)
      
所有节点的平衡因子 ∈ {-1, 0, 1}

非 AVL 树示例:
            [30] (2)           ← 平衡因子 = 2，失衡！
           /
        [20] (1)
        /
     [10] (0)
     
30 的左子树高度 2，右子树高度 0，差值 = 2 > 1
```

## 2. 来源

AVL 由 Adelson-Velsky 与 Landis 提出，主要目标是严格控制树高，  
保证查询性能稳定在 `O(log n)`。

## 3. 知识点

### 3.1 平衡因子

```
平衡因子计算:

balance_factor(node) = height(node.left) - height(node.right)

          [30]
         /    \
       [20]   [40]
       /  \      \
    [10] [25]    [50]
    
height(10) = 0    height(null) = -1
height(25) = 0
height(20) = 1    BF(20) = 0 - 0 = 0
height(50) = 0
height(40) = 1    BF(40) = -1 - 0 = -1
height(30) = 2    BF(30) = 1 - 1 = 0
```

### 3.2 四种失衡模式与旋转

```
LL 型（左左失衡）→ 右旋:

Before:            After:
      [30] (2)          [20] (0)
      /                 /    \
   [20] (1)          [10]   [30] (0)
   /                         /
[10] (0)                 [25]

失衡原因: 30 的左子树的左子树插入
修复: 对 30 右旋


RR 型（右右失衡）→ 左旋:

Before:            After:
[20] (-2)                [30] (0)
    \                   /    \
   [30] (-1)         [20]   [40]
       \               /
      [40] (0)      [25]

失衡原因: 20 的右子树的右子树插入
修复: 对 20 左旋


LR 型（左右失衡）→ 先左旋再右旋:

Before:            Step 1: 对 20 左旋
      [30] (2)           [30] (2)
      /                  /
   [20] (-1)          [25] (1)
       \              /
      [25] (0)     [20] (0)
      
                   Step 2: 对 30 右旋
                   
                   After:
                      [25] (0)
                      /    \
                   [20]   [30]


RL 型（右左失衡）→ 先右旋再左旋:

Before:            Step 1: 对 30 右旋
[20] (-2)          [20] (-2)
    \                  \
   [30] (1)           [25] (-1)
    /                     \
 [25] (0)               [30] (0)
 
                   Step 2: 对 20 左旋
                   
                   After:
                      [25] (0)
                      /    \
                   [20]   [30]
```

**旋转操作详解:**
```
右旋（Rotate Right）:

       y                    x
      / \                  / \
     x   C      →         A   y
    / \                      / \
   A   B                    B   C

左旋（Rotate Left）:

     x                      y
    / \                    / \
   A   y        →         x   C
      / \                / \
     B   C              A   B
```

### 3.3 插入与删除

```
插入流程:
1. 按 BST 规则插入新节点
2. 从插入点向上更新高度
3. 检查每个祖先节点的平衡因子
4. 发现失衡则进行相应旋转
5. 继续向上直到根节点

删除流程:
1. 按 BST 规则删除节点
2. 从删除点向上更新高度
3. 检查每个祖先节点的平衡因子
4. 发现失衡则进行相应旋转
5. 继续向上直到根节点
```

## 4. 数据结构

示例代码：`src/main/java/org/byron4j/cookbook/algrithms/tree/AvlTreeDemo.java`

示例实现：

- 插入节点
- 自动计算高度
- 失衡后旋转修复
- 中序遍历输出

```java
class AVLNode {
    int value;
    AVLNode left, right;
    int height;  // 缓存高度，避免重复计算
    
    int getHeight(AVLNode node) {
        return node == null ? -1 : node.height;
    }
    
    int getBalance(AVLNode node) {
        return node == null ? 0 : 
            getHeight(node.left) - getHeight(node.right);
    }
    
    AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode B = x.right;
        
        x.right = y;
        y.left = B;
        
        y.height = 1 + Math.max(getHeight(y.left), getHeight(y.right));
        x.height = 1 + Math.max(getHeight(x.left), getHeight(x.right));
        
        return x;
    }
}
```

## 5. 最佳实践

1. **查询远多于更新时可优先 AVL**。  
2. **节点中缓存高度减少重复计算**。  
3. **旋转逻辑要模块化，避免分支混乱**。  
4. **先保证正确再优化常数开销**。  
5. **通过构造 LL/RR/LR/RL 输入做回归测试**。

### AVL vs 红黑树选择

```
决策树:

读操作频率高？
   ├── 是 → AVL树
   │         - 更严格的平衡
   │         - 查询更快
   │         - 适合数据库索引
   │
   └── 否 → 写操作频率高？
              ├── 是 → 红黑树
              │         - 旋转次数少
              │         - 插入删除更快
              │         - 适合内存数据结构
              │
              └── 否 → 两者皆可
```

## 运行示例

```bash
java -cp out org.byron4j.cookbook.algrithms.tree.AvlTreeDemo
```