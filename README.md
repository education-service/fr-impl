
# 人脸识别算法实现

> 实现PCA、LDA和LPP三种识别算法。

## 实现原理

> 使用训练数据训练后，使用不同的方法，通过PCA得到特征空间“eigenfaces”，通过LDA得到特征空间“fisherfaces”，以及通过LPP得到特征空间“laplacianfaces”。
 
## 距离实现

> 本项目实现了三种距离判定标准：CosineDissimilarity, L1Distance和EuclideanDistance，同时也实现了KNN分类算法。

## 测试人脸库

> 本项目使用两类测试库：ORL和Yale，相关信息如下：

`ORL人脸库`：[http://www.cl.cam.ac.uk/research/dtg/attarchive/facedatabase.html](http://www.cl.cam.ac.uk/research/dtg/attarchive/facedatabase.html)

`Yale人脸库`：[http://vision.ucsd.edu/content/yale-face-database](http://vision.ucsd.edu/content/yale-face-database)