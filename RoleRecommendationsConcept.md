# Introduction #

The aim of context sensitive role recommendation is to reduce the set of recommended thematic roles for the selected signature element. This way we want to simplify the usage of iDocIt!.

# Issues #

The following issues refer to this feature:

[Issue 79](http://code.google.com/p/idocit/issues/detail?id=79)

# Concept #

The basic idea is to define rules which enable or disable the recommendation of a thematic role for a given signature element.

## Kinds of Rules ##

**Role-based rules:** this kind of rules define whether a thematic role is allowed to be defined only on interface-level, only on operation-level or both. A role-based rule is an attribute of a thematic role. Each thematic role as exactly one role-based rule.

**Grid-based rules:** grid-based rules are used to define when a thematic role is shown as recommendation within a thematic grid. A Grid-based rule is an attribute of a thematic role in a thematic grid. Each thematic role in the grid has exactly one grid-based rule.

## Grammar of Rule Language in BNF ##

```
Rule := Predicate

Predicate :=  SimplePredicate 
            | CompositePredicate
            | NegatedPredicate 

SimplePredicate := isSingular (Role, SignatureElement)
               | isPlural (Role, SignatureElement)
               | hasAttributes (Role, SignatureElement)
               | exists ( Role, SignatureElement)
               | default

CompositePredicate := ( Predicate BinaryOperator Predicate)

NegatedPredicate := not ( Predicate)

BinaryOperator := AND | OR

Role := AGENT | ACTION | COMPARISON | ...

SignatureElement := DataStructure from de.akra.idocit.common
```

# Implementation #

## Graphical User Interface and Use Model ##

### Preference Page: Thematic Roles ###

![http://idocit.googlecode.com/svn/images/draft_ss_thematic_role_pp.png](http://idocit.googlecode.com/svn/images/draft_ss_thematic_role_pp.png)

### Preference Page: Thematic Grids ###

  * We add a new button "Edit grid-based rule" below the table of thematic roles. After pressing this button, the rule is passed to a modal dialog (see below).
  * The rule should be entered in a small modal dialog with CANCEL- and OK-buttons. It is validated after pressing the OK-button.
  * The thematic role table will be extended with a new column "rule" which displays the rule for each thematic role. This way the user could read the rules quickly and decide whether they are ok or not.

### EditorPart ###

  * We have two use cases for the rule execution: grid-recommendation and and thematic role-combobox.
  * The thematic role combo will the replaced by a two level-contextmenu: the first level contains all first level recommendations (see Services) and an item "More roles >". Via this item, the remaining roles could be accessed. All roles are sorted alphabetically.

### Services ###

  * We implement the [RuleService](http://code.google.com/p/idocit/source/browse/trunk/de.akra.idocit.common/src/main/java/de/akra/idocit/common/services/RuleService.java) which executes the rules.
  * The RuleService bases on the [Java Scripting API](http://download.oracle.com/javase/6/docs/api/javax/script/package-summary.html) and is realized in Java Script.
  * The predicates are implemented as Java Script-functions and stored within its own js-file in the src/main/resources-folders.

# TODOs #
> NONE