/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */
package com.github.javaparser.ast.visitor;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import java.util.List;
import java.util.Optional;

/**
 * A visitor that calculates deep node equality by comparing all properties and child nodes of the node.
 *
 * @author Julio Vilmar Gesser
 */
public class EqualsVisitor implements GenericVisitor<Boolean, Visitable> {

    private static final EqualsVisitor SINGLETON = new EqualsVisitor();

    public static boolean equals(final Node n1, final Node n2) {
        return SINGLETON.nodeEquals(n1, n2);
    }

    private EqualsVisitor() {
    // hide constructor
    }

    /**
     * Check for equality that can be applied to each kind of node,
     * to not repeat it in every method we store that here.
     */
    private boolean commonNodeEquality(Node n1, Node n2) {
        if (!nodeEquals(n1.getComment(), n2.getComment())) {
            return false;
        }
        return nodesEquals(n1.getOrphanComments(), n2.getOrphanComments());
    }

    private <T extends Node> boolean nodesEquals(final List<T> nodes1, final List<T> nodes2) {
        if (nodes1 == null) {
            return nodes2 == null;
        } else if (nodes2 == null) {
            return false;
        }
        if (nodes1.size() != nodes2.size()) {
            return false;
        }
        for (int i = 0; i < nodes1.size(); i++) {
            if (!nodeEquals(nodes1.get(i), nodes2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private <N extends Node> boolean nodesEquals(NodeList<N> n1, NodeList<N> n2) {
        if (n1 == n2) {
            return true;
        }
        if (n1 == null || n2 == null) {
            return false;
        }
        if (n1.size() != n2.size()) {
            return false;
        }
        for (int i = 0; i < n1.size(); i++) {
            if (!nodeEquals(n1.get(i), n2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private <T extends Node> boolean nodeEquals(final T n1, final T n2) {
        if (n1 == n2) {
            return true;
        }
        if (n1 == null || n2 == null) {
            return false;
        }
        if (n1.getClass() != n2.getClass()) {
            return false;
        }
        if (!commonNodeEquality(n1, n2)) {
            return false;
        }
        return n1.accept(this, n2);
    }

    private <T extends Node> boolean nodeEquals(final Optional<T> n1, final Optional<T> n2) {
        return nodeEquals(n1.orElse(null), n2.orElse(null));
    }

    private <T extends Node> boolean nodesEquals(final Optional<NodeList<T>> n1, final Optional<NodeList<T>> n2) {
        return nodesEquals(n1.orElse(null), n2.orElse(null));
    }

    private boolean objEquals(final Object n1, final Object n2) {
        if (n1 == n2) {
            return true;
        }
        if (n1 == null || n2 == null) {
            return false;
        }
        return n1.equals(n2);
    }

    @Override
    public Boolean visit(CompilationUnit n, Visitable arg) {
        final CompilationUnit n2 = (CompilationUnit) arg;
        if (!nodesEquals(n.getImports(), n2.getImports()))
            return false;
        if (!nodeEquals(n.getPackageDeclaration(), n2.getPackageDeclaration()))
            return false;
        if (!nodesEquals(n.getTypes(), n2.getTypes()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(PackageDeclaration n, Visitable arg) {
        final PackageDeclaration n2 = (PackageDeclaration) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(TypeParameter n, Visitable arg) {
        final TypeParameter n2 = (TypeParameter) arg;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getTypeBound(), n2.getTypeBound()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(LineComment n, Visitable arg) {
        final LineComment n2 = (LineComment) arg;
        if (!objEquals(n.getContent(), n2.getContent()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(BlockComment n, Visitable arg) {
        final BlockComment n2 = (BlockComment) arg;
        if (!objEquals(n.getContent(), n2.getContent()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ClassOrInterfaceDeclaration n, Visitable arg) {
        final ClassOrInterfaceDeclaration n2 = (ClassOrInterfaceDeclaration) arg;
        if (!nodesEquals(n.getExtendedTypes(), n2.getExtendedTypes()))
            return false;
        if (!nodesEquals(n.getImplementedTypes(), n2.getImplementedTypes()))
            return false;
        if (!objEquals(n.isInterface(), n2.isInterface()))
            return false;
        if (!nodesEquals(n.getTypeParameters(), n2.getTypeParameters()))
            return false;
        if (!nodesEquals(n.getMembers(), n2.getMembers()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(EnumDeclaration n, Visitable arg) {
        final EnumDeclaration n2 = (EnumDeclaration) arg;
        if (!nodesEquals(n.getEntries(), n2.getEntries()))
            return false;
        if (!nodesEquals(n.getImplementedTypes(), n2.getImplementedTypes()))
            return false;
        if (!nodesEquals(n.getMembers(), n2.getMembers()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(EnumConstantDeclaration n, Visitable arg) {
        final EnumConstantDeclaration n2 = (EnumConstantDeclaration) arg;
        if (!nodesEquals(n.getArguments(), n2.getArguments()))
            return false;
        if (!nodesEquals(n.getClassBody(), n2.getClassBody()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(AnnotationDeclaration n, Visitable arg) {
        final AnnotationDeclaration n2 = (AnnotationDeclaration) arg;
        if (!nodesEquals(n.getMembers(), n2.getMembers()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(AnnotationMemberDeclaration n, Visitable arg) {
        final AnnotationMemberDeclaration n2 = (AnnotationMemberDeclaration) arg;
        if (!nodeEquals(n.getDefaultValue(), n2.getDefaultValue()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(FieldDeclaration n, Visitable arg) {
        final FieldDeclaration n2 = (FieldDeclaration) arg;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodesEquals(n.getVariables(), n2.getVariables()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(VariableDeclarator n, Visitable arg) {
        final VariableDeclarator n2 = (VariableDeclarator) arg;
        if (!nodeEquals(n.getInitializer(), n2.getInitializer()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ConstructorDeclaration n, Visitable arg) {
        final ConstructorDeclaration n2 = (ConstructorDeclaration) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getParameters(), n2.getParameters()))
            return false;
        if (!nodesEquals(n.getThrownExceptions(), n2.getThrownExceptions()))
            return false;
        if (!nodesEquals(n.getTypeParameters(), n2.getTypeParameters()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(MethodDeclaration n, Visitable arg) {
        final MethodDeclaration n2 = (MethodDeclaration) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!objEquals(n.isDefault(), n2.isDefault()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodesEquals(n.getParameters(), n2.getParameters()))
            return false;
        if (!nodesEquals(n.getThrownExceptions(), n2.getThrownExceptions()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodesEquals(n.getTypeParameters(), n2.getTypeParameters()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(Parameter n, Visitable arg) {
        final Parameter n2 = (Parameter) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!objEquals(n.isVarArgs(), n2.isVarArgs()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(EmptyMemberDeclaration n, Visitable arg) {
        final EmptyMemberDeclaration n2 = (EmptyMemberDeclaration) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(InitializerDeclaration n, Visitable arg) {
        final InitializerDeclaration n2 = (InitializerDeclaration) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!objEquals(n.isStatic(), n2.isStatic()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(JavadocComment n, Visitable arg) {
        final JavadocComment n2 = (JavadocComment) arg;
        if (!objEquals(n.getContent(), n2.getContent()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ClassOrInterfaceType n, Visitable arg) {
        final ClassOrInterfaceType n2 = (ClassOrInterfaceType) arg;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getScope(), n2.getScope()))
            return false;
        if (!nodesEquals(n.getTypeArguments(), n2.getTypeArguments()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(PrimitiveType n, Visitable arg) {
        final PrimitiveType n2 = (PrimitiveType) arg;
        if (!objEquals(n.getType(), n2.getType()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ArrayType n, Visitable arg) {
        final ArrayType n2 = (ArrayType) arg;
        if (!nodeEquals(n.getComponentType(), n2.getComponentType()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ArrayCreationLevel n, Visitable arg) {
        final ArrayCreationLevel n2 = (ArrayCreationLevel) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getDimension(), n2.getDimension()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(IntersectionType n, Visitable arg) {
        final IntersectionType n2 = (IntersectionType) arg;
        if (!nodesEquals(n.getElements(), n2.getElements()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(UnionType n, Visitable arg) {
        final UnionType n2 = (UnionType) arg;
        if (!nodesEquals(n.getElements(), n2.getElements()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(VoidType n, Visitable arg) {
        final VoidType n2 = (VoidType) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(WildcardType n, Visitable arg) {
        final WildcardType n2 = (WildcardType) arg;
        if (!nodeEquals(n.getExtendedTypes(), n2.getExtendedTypes()))
            return false;
        if (!nodeEquals(n.getSuperTypes(), n2.getSuperTypes()))
            return false;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(UnknownType n, Visitable arg) {
        final UnknownType n2 = (UnknownType) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ArrayAccessExpr n, Visitable arg) {
        final ArrayAccessExpr n2 = (ArrayAccessExpr) arg;
        if (!nodeEquals(n.getIndex(), n2.getIndex()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ArrayCreationExpr n, Visitable arg) {
        final ArrayCreationExpr n2 = (ArrayCreationExpr) arg;
        if (!nodeEquals(n.getElementType(), n2.getElementType()))
            return false;
        if (!nodeEquals(n.getInitializer(), n2.getInitializer()))
            return false;
        if (!nodesEquals(n.getLevels(), n2.getLevels()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ArrayInitializerExpr n, Visitable arg) {
        final ArrayInitializerExpr n2 = (ArrayInitializerExpr) arg;
        if (!nodesEquals(n.getValues(), n2.getValues()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(AssignExpr n, Visitable arg) {
        final AssignExpr n2 = (AssignExpr) arg;
        if (!objEquals(n.getOperator(), n2.getOperator()))
            return false;
        if (!nodeEquals(n.getTarget(), n2.getTarget()))
            return false;
        if (!nodeEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(BinaryExpr n, Visitable arg) {
        final BinaryExpr n2 = (BinaryExpr) arg;
        if (!nodeEquals(n.getLeft(), n2.getLeft()))
            return false;
        if (!objEquals(n.getOperator(), n2.getOperator()))
            return false;
        if (!nodeEquals(n.getRight(), n2.getRight()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(CastExpr n, Visitable arg) {
        final CastExpr n2 = (CastExpr) arg;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ClassExpr n, Visitable arg) {
        final ClassExpr n2 = (ClassExpr) arg;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ConditionalExpr n, Visitable arg) {
        final ConditionalExpr n2 = (ConditionalExpr) arg;
        if (!nodeEquals(n.getCondition(), n2.getCondition()))
            return false;
        if (!nodeEquals(n.getElseExpr(), n2.getElseExpr()))
            return false;
        if (!nodeEquals(n.getThenExpr(), n2.getThenExpr()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(EnclosedExpr n, Visitable arg) {
        final EnclosedExpr n2 = (EnclosedExpr) arg;
        if (!nodeEquals(n.getInner(), n2.getInner()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(FieldAccessExpr n, Visitable arg) {
        final FieldAccessExpr n2 = (FieldAccessExpr) arg;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getScope(), n2.getScope()))
            return false;
        if (!nodesEquals(n.getTypeArguments(), n2.getTypeArguments()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(InstanceOfExpr n, Visitable arg) {
        final InstanceOfExpr n2 = (InstanceOfExpr) arg;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(StringLiteralExpr n, Visitable arg) {
        final StringLiteralExpr n2 = (StringLiteralExpr) arg;
        if (!objEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(IntegerLiteralExpr n, Visitable arg) {
        final IntegerLiteralExpr n2 = (IntegerLiteralExpr) arg;
        if (!objEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(LongLiteralExpr n, Visitable arg) {
        final LongLiteralExpr n2 = (LongLiteralExpr) arg;
        if (!objEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(CharLiteralExpr n, Visitable arg) {
        final CharLiteralExpr n2 = (CharLiteralExpr) arg;
        if (!objEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(DoubleLiteralExpr n, Visitable arg) {
        final DoubleLiteralExpr n2 = (DoubleLiteralExpr) arg;
        if (!objEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(BooleanLiteralExpr n, Visitable arg) {
        final BooleanLiteralExpr n2 = (BooleanLiteralExpr) arg;
        if (!objEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(NullLiteralExpr n, Visitable arg) {
        final NullLiteralExpr n2 = (NullLiteralExpr) arg;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(MethodCallExpr n, Visitable arg) {
        final MethodCallExpr n2 = (MethodCallExpr) arg;
        if (!nodesEquals(n.getArguments(), n2.getArguments()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getScope(), n2.getScope()))
            return false;
        if (!nodesEquals(n.getTypeArguments(), n2.getTypeArguments()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(NameExpr n, Visitable arg) {
        final NameExpr n2 = (NameExpr) arg;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ObjectCreationExpr n, Visitable arg) {
        final ObjectCreationExpr n2 = (ObjectCreationExpr) arg;
        if (!nodesEquals(n.getAnonymousClassBody(), n2.getAnonymousClassBody()))
            return false;
        if (!nodesEquals(n.getArguments(), n2.getArguments()))
            return false;
        if (!nodeEquals(n.getScope(), n2.getScope()))
            return false;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodesEquals(n.getTypeArguments(), n2.getTypeArguments()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(Name n, Visitable arg) {
        final Name n2 = (Name) arg;
        if (!objEquals(n.getIdentifier(), n2.getIdentifier()))
            return false;
        if (!nodeEquals(n.getQualifier(), n2.getQualifier()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(SimpleName n, Visitable arg) {
        final SimpleName n2 = (SimpleName) arg;
        if (!objEquals(n.getIdentifier(), n2.getIdentifier()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ThisExpr n, Visitable arg) {
        final ThisExpr n2 = (ThisExpr) arg;
        if (!nodeEquals(n.getClassExpr(), n2.getClassExpr()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(SuperExpr n, Visitable arg) {
        final SuperExpr n2 = (SuperExpr) arg;
        if (!nodeEquals(n.getClassExpr(), n2.getClassExpr()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(UnaryExpr n, Visitable arg) {
        final UnaryExpr n2 = (UnaryExpr) arg;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!objEquals(n.getOperator(), n2.getOperator()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(VariableDeclarationExpr n, Visitable arg) {
        final VariableDeclarationExpr n2 = (VariableDeclarationExpr) arg;
        if (!nodesEquals(n.getAnnotations(), n2.getAnnotations()))
            return false;
        if (!objEquals(n.getModifiers(), n2.getModifiers()))
            return false;
        if (!nodesEquals(n.getVariables(), n2.getVariables()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(MarkerAnnotationExpr n, Visitable arg) {
        final MarkerAnnotationExpr n2 = (MarkerAnnotationExpr) arg;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(SingleMemberAnnotationExpr n, Visitable arg) {
        final SingleMemberAnnotationExpr n2 = (SingleMemberAnnotationExpr) arg;
        if (!nodeEquals(n.getMemberValue(), n2.getMemberValue()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(NormalAnnotationExpr n, Visitable arg) {
        final NormalAnnotationExpr n2 = (NormalAnnotationExpr) arg;
        if (!nodesEquals(n.getPairs(), n2.getPairs()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(MemberValuePair n, Visitable arg) {
        final MemberValuePair n2 = (MemberValuePair) arg;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getValue(), n2.getValue()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ExplicitConstructorInvocationStmt n, Visitable arg) {
        final ExplicitConstructorInvocationStmt n2 = (ExplicitConstructorInvocationStmt) arg;
        if (!nodesEquals(n.getArguments(), n2.getArguments()))
            return false;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!objEquals(n.isThis(), n2.isThis()))
            return false;
        if (!nodesEquals(n.getTypeArguments(), n2.getTypeArguments()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(LocalClassDeclarationStmt n, Visitable arg) {
        final LocalClassDeclarationStmt n2 = (LocalClassDeclarationStmt) arg;
        if (!nodeEquals(n.getClassDeclaration(), n2.getClassDeclaration()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(AssertStmt n, Visitable arg) {
        final AssertStmt n2 = (AssertStmt) arg;
        if (!nodeEquals(n.getCheck(), n2.getCheck()))
            return false;
        if (!nodeEquals(n.getMessage(), n2.getMessage()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(BlockStmt n, Visitable arg) {
        final BlockStmt n2 = (BlockStmt) arg;
        if (!nodesEquals(n.getStatements(), n2.getStatements()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(LabeledStmt n, Visitable arg) {
        final LabeledStmt n2 = (LabeledStmt) arg;
        if (!nodeEquals(n.getLabel(), n2.getLabel()))
            return false;
        if (!nodeEquals(n.getStatement(), n2.getStatement()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(EmptyStmt n, Visitable arg) {
        final EmptyStmt n2 = (EmptyStmt) arg;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ExpressionStmt n, Visitable arg) {
        final ExpressionStmt n2 = (ExpressionStmt) arg;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(SwitchStmt n, Visitable arg) {
        final SwitchStmt n2 = (SwitchStmt) arg;
        if (!nodesEquals(n.getEntries(), n2.getEntries()))
            return false;
        if (!nodeEquals(n.getSelector(), n2.getSelector()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(SwitchEntryStmt n, Visitable arg) {
        final SwitchEntryStmt n2 = (SwitchEntryStmt) arg;
        if (!nodeEquals(n.getLabel(), n2.getLabel()))
            return false;
        if (!nodesEquals(n.getStatements(), n2.getStatements()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(BreakStmt n, Visitable arg) {
        final BreakStmt n2 = (BreakStmt) arg;
        if (!nodeEquals(n.getLabel(), n2.getLabel()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ReturnStmt n, Visitable arg) {
        final ReturnStmt n2 = (ReturnStmt) arg;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(IfStmt n, Visitable arg) {
        final IfStmt n2 = (IfStmt) arg;
        if (!nodeEquals(n.getCondition(), n2.getCondition()))
            return false;
        if (!nodeEquals(n.getElseStmt(), n2.getElseStmt()))
            return false;
        if (!nodeEquals(n.getThenStmt(), n2.getThenStmt()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(WhileStmt n, Visitable arg) {
        final WhileStmt n2 = (WhileStmt) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!nodeEquals(n.getCondition(), n2.getCondition()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ContinueStmt n, Visitable arg) {
        final ContinueStmt n2 = (ContinueStmt) arg;
        if (!nodeEquals(n.getLabel(), n2.getLabel()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(DoStmt n, Visitable arg) {
        final DoStmt n2 = (DoStmt) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!nodeEquals(n.getCondition(), n2.getCondition()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ForeachStmt n, Visitable arg) {
        final ForeachStmt n2 = (ForeachStmt) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!nodeEquals(n.getIterable(), n2.getIterable()))
            return false;
        if (!nodeEquals(n.getVariable(), n2.getVariable()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ForStmt n, Visitable arg) {
        final ForStmt n2 = (ForStmt) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!nodeEquals(n.getCompare(), n2.getCompare()))
            return false;
        if (!nodesEquals(n.getInitialization(), n2.getInitialization()))
            return false;
        if (!nodesEquals(n.getUpdate(), n2.getUpdate()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ThrowStmt n, Visitable arg) {
        final ThrowStmt n2 = (ThrowStmt) arg;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(SynchronizedStmt n, Visitable arg) {
        final SynchronizedStmt n2 = (SynchronizedStmt) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!nodeEquals(n.getExpression(), n2.getExpression()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(TryStmt n, Visitable arg) {
        final TryStmt n2 = (TryStmt) arg;
        if (!nodesEquals(n.getCatchClauses(), n2.getCatchClauses()))
            return false;
        if (!nodeEquals(n.getFinallyBlock(), n2.getFinallyBlock()))
            return false;
        if (!nodesEquals(n.getResources(), n2.getResources()))
            return false;
        if (!nodeEquals(n.getTryBlock(), n2.getTryBlock()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(CatchClause n, Visitable arg) {
        final CatchClause n2 = (CatchClause) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!nodeEquals(n.getParameter(), n2.getParameter()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(LambdaExpr n, Visitable arg) {
        final LambdaExpr n2 = (LambdaExpr) arg;
        if (!nodeEquals(n.getBody(), n2.getBody()))
            return false;
        if (!objEquals(n.isEnclosingParameters(), n2.isEnclosingParameters()))
            return false;
        if (!nodesEquals(n.getParameters(), n2.getParameters()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(MethodReferenceExpr n, Visitable arg) {
        final MethodReferenceExpr n2 = (MethodReferenceExpr) arg;
        if (!objEquals(n.getIdentifier(), n2.getIdentifier()))
            return false;
        if (!nodeEquals(n.getScope(), n2.getScope()))
            return false;
        if (!nodesEquals(n.getTypeArguments(), n2.getTypeArguments()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(TypeExpr n, Visitable arg) {
        final TypeExpr n2 = (TypeExpr) arg;
        if (!nodeEquals(n.getType(), n2.getType()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(ImportDeclaration n, Visitable arg) {
        final ImportDeclaration n2 = (ImportDeclaration) arg;
        if (!objEquals(n.isAsterisk(), n2.isAsterisk()))
            return false;
        if (!objEquals(n.isStatic(), n2.isStatic()))
            return false;
        if (!nodeEquals(n.getName(), n2.getName()))
            return false;
        if (!nodeEquals(n.getComment(), n2.getComment()))
            return false;
        return true;
    }

    @Override
    public Boolean visit(NodeList n, Visitable arg) {
        return nodesEquals((NodeList<Node>) n, (NodeList<Node>) arg);
    }
}

