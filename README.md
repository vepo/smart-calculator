# Smart Calculator 

Criar um pequeno framework contendo formas avançadas de configuração!!!

```
> 2 + 5
= 7
```

Adicionar hookpoints!!!!


```javascript
function onExpression(value1, operator, value2) {
    return null;
}


function onExpression(value1, operator, value2) {
    print("EXPR:" + value1 + operator + value2);
    return eval(value1 + operator + value2)
}
```